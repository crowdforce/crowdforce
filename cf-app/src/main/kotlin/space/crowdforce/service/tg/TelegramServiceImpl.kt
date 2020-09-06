package space.crowdforce.service.tg

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("prod")
class TelegramServiceImpl() : TelegramService {

}
