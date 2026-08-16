package com.github.bovvver.usermanagement.verification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bovvver.BaseIntegrationTest;
import com.github.bovvver.shared.CurrentUser;
import com.github.bovvver.usermanagement.User;
import com.github.bovvver.usermanagement.UserRepository;
import com.github.bovvver.vo.Email;
import com.github.bovvver.vo.UserId;
import com.github.bovvver.vo.VerificationStatus;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VerificationProofRESTIT extends BaseIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private MinioClient minioClient;

    @MockitoBean
    private CurrentUser currentUser;

    private static final UUID USER_UUID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE users CASCADE");
    }

    private void createTestUserAndVerification(VerificationStatus status, String proofUrl) {
        User user = User.create(UserId.of(USER_UUID), new Email("test@example.com"), "John", "Doe");
        if (proofUrl != null) {
            user.addVerificationProof(VerificationProof.of(proofUrl));
        }
        if (status == VerificationStatus.VERIFIED) {
            user.verify();
        } else if (status == VerificationStatus.REJECTED) {
            user.reject();
        }
        userRepository.save(user);
    }

    @Test
    void shouldGetPresignedUploadUrlSuccessfully() throws Exception {
        createTestUserAndVerification(VerificationStatus.PENDING, null);

        when(currentUser.getId()).thenReturn(UserId.of(USER_UUID));
        doReturn("http://minio/upload-url").when(minioClient).getPresignedObjectUrl(any());

        PresignedUploadUrlRequest request = new PresignedUploadUrlRequest("proof.png", "image/png");

        mockMvc.perform(post(VerificationProofREST.GET_PRESIGNED_UPLOAD_URL)
                        .header("X-Keycloak-API-Key", "test-api-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uploadUrl").value("http://minio/upload-url"))
                .andExpect(jsonPath("$.fileId").value(org.hamcrest.Matchers.containsString("verification/" + USER_UUID)));
    }

    @Test
    void shouldGetPresignedGetUrlsSuccessfully() throws Exception {
        createTestUserAndVerification(VerificationStatus.PENDING, "verification/" + USER_UUID + "/proof.png");

        doReturn("http://minio/get-url").when(minioClient).getPresignedObjectUrl(any());

        mockMvc.perform(get(VerificationProofREST.GET_PRESIGNED_GET_URLS, USER_UUID)
                        .header("X-Keycloak-API-Key", "test-api-key")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.proofUrls[0]").value("http://minio/get-url"));
    }

    @Test
    void shouldSendVerificationDataSuccessfully() throws Exception {
        createTestUserAndVerification(VerificationStatus.PENDING, null);

        when(currentUser.getId()).thenReturn(UserId.of(USER_UUID));

        VerificationDataRequest request = new VerificationDataRequest(List.of("http://example.com/proof"));

        mockMvc.perform(post(VerificationProofREST.SEND_VERIFICATION_DATA_URL)
                        .header("X-Keycloak-API-Key", "test-api-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(USER_UUID.toString()));

        User updated = userRepository.findById(UserId.of(USER_UUID)).orElseThrow();
        assertThat(updated.getVerificationProof().url()).isEqualTo("http://example.com/proof");
        assertThat(updated.getVerificationProof().uploadedAt()).isNotNull();
    }

    @Test
    void shouldVerifyUserSuccessfully() throws Exception {
        createTestUserAndVerification(VerificationStatus.PENDING, "http://example.com/proof");

        mockMvc.perform(post(VerificationProofREST.VERIFY_USER_URL, USER_UUID)
                        .header("X-Keycloak-API-Key", "test-api-key")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        User updated = userRepository.findById(UserId.of(USER_UUID)).orElseThrow();
        assertThat(updated.getIdentityStatus()).isEqualTo(VerificationStatus.VERIFIED);
    }

    @Test
    void shouldRejectUserSuccessfully() throws Exception {
        createTestUserAndVerification(VerificationStatus.PENDING, null);

        mockMvc.perform(post(VerificationProofREST.REJECT_USER_URL, USER_UUID)
                        .header("X-Keycloak-API-Key", "test-api-key")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        User updated = userRepository.findById(UserId.of(USER_UUID)).orElseThrow();
        assertThat(updated.getIdentityStatus()).isEqualTo(VerificationStatus.REJECTED);
    }
}
