package space.byeoruk.b.domain.notification.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository
import space.byeoruk.b.domain.member.details.MemberDetails
import space.byeoruk.b.domain.notification.dto.NotificationDto
import space.byeoruk.b.domain.notification.entity.Notification
import space.byeoruk.b.domain.notification.entity.QNotification
import java.time.LocalDateTime
import java.util.function.LongSupplier

@Repository
class NotificationCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory
): NotificationCustomRepository {
    private val notification = QNotification.notification

    /**
     * Read by pagination.
     *
     * @param request 요청 정보
     * @param memberDetails 사용자 인증 객체
     * @param pageable 페이징 처리 객체
     * @return 알림 목록
     */
    override fun findAllByPage(
        request: NotificationDto.ReadRequest,
        memberDetails: MemberDetails,
        pageable: Pageable
    ): Page<Notification> {
        //  조회 쿼리
        val query = queryFactory.selectFrom(notification)
            .where(
                equalsReceiver(memberDetails),
                beforeSentAt(request.standardDate),
                equalsIsRead(request.isRead)
            )

        //  카운트 쿼리
        val countQuery = queryFactory.select(notification.uid.count())
            .from(notification)
            .where(
                equalsReceiver(memberDetails),
                beforeSentAt(request.standardDate),
                equalsIsRead(request.isRead)
            )

        val notifications = query.offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(notification.sentAt.desc())
            .fetch()

        return PageableExecutionUtils.getPage(notifications, pageable) {
            countQuery.fetchOne() ?: 0L
        }
    }

    /**
     * 알림 일괄 삭제
     *
     * @param request 요청 정보
     * @param memberDetails 사용자 인증 객체
     * @return 삭제 개수
     */
    override fun deleteByRequest(request: NotificationDto.DeleteRequest, memberDetails: MemberDetails): Long =
        queryFactory.delete(notification)
            .where(
                notification.receiver.uid.eq(memberDetails.getIdentifier()),
                inUids(request.uids),
                equalsIsRead(request.onlyRead),
            )
            .execute()

    /**
     * 받는자가 본인인지 조회 조건
     *
     * @param memberDetails 사용자 인증 객체
     * @return 조회 조건
     */
    private fun equalsReceiver(memberDetails: MemberDetails): BooleanExpression {
        return notification.receiver.uid.eq(memberDetails.getIdentifier())
    }

    /**
     * 기준일로 부터 이전 날짜의 알림 조회 조건
     *
     * @param standardDate 기준 날짜
     * @return 조회 조건
     */
    private fun beforeSentAt(standardDate: LocalDateTime?): BooleanExpression? {
        return standardDate?.let { notification.sentAt.before(it) }
    }

    /**
     * 여러 UID 조회 조건
     *
     * @param uids UID 목록
     * @return 조회 조건
     */
    private fun inUids(uids: List<Long>? = emptyList()): BooleanExpression? =
        if(!uids.isNullOrEmpty())
            notification.uid.`in`(uids)
        else
            null

    /**
     * 읽음 여부 조회 조건
     *
     * @param isRead 읽음 여부
     * @return 조회 조건
     */
    private fun equalsIsRead(isRead: Boolean?): BooleanExpression? {
        return isRead?.let { notification.isRead.eq(it) }
    }
}