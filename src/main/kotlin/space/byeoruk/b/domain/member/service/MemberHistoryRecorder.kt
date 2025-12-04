package space.byeoruk.b.domain.member.service

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import space.byeoruk.b.domain.member.dto.MemberHistoryDto
import space.byeoruk.b.domain.member.entity.Member
import space.byeoruk.b.domain.member.entity.MemberHistory
import space.byeoruk.b.domain.member.repository.MemberHistoryRepository
import tools.jackson.databind.ObjectMapper

@Service
class MemberHistoryRecorder(
    private val memberHistoryRepository: MemberHistoryRepository,
    private val httpRequest: HttpServletRequest,
    private val objectMapper: ObjectMapper
) {
    @Transactional
    fun record(member: Member, map: MemberHistoryDto.RecordMap) {
        val beforeJson = map.before?.let { objectMapper.writeValueAsString(it) } ?: ""
        val afterJson = map.after?.let { objectMapper.writeValueAsString(it) } ?: ""

        map.ipAddress = httpRequest.remoteAddr
        map.userAgent = httpRequest.getHeader("User-Agent")

        memberHistoryRepository.save(MemberHistory(member, beforeJson, afterJson, map))
    }
}