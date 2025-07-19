import org.springframework.web.bind.annotation.RequestParam;
import com.bootcampbox.server.service.EmailVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HelloController {

    // ====== [개발용] 이메일 강제 인증 API ======
    // 실제 운영 배포 전 반드시 삭제!
    @Autowired
    private EmailVerificationService emailVerificationService;

    @PostMapping("/api/dev/verify-email")
    public Map<String, Object> verifyEmailDev(@RequestParam String email) {
        emailVerificationService.forceVerifyEmail(email);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("email", email);
        return result;
    }
} 