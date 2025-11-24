package tutorial.modtutorial.service.impl;

import com.jayway.jsonpath.JsonPath;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.stereotype.Service;
import tutorial.modtutorial.configuration.ApplicationProperties;
import tutorial.modtutorial.domain.dto.sms.SMSDTO;
import tutorial.modtutorial.domain.entity.SMS;
import tutorial.modtutorial.repository.SMSRepository;
import tutorial.modtutorial.service.SMSService;


@Service
public class SMSServiceImpl implements SMSService {
    private final ApplicationProperties.Sms props;
    private final SMSRepository smsRepository;
    private String token = "";
    private boolean retried = false;

    public SMSServiceImpl(ApplicationProperties applicationProperties, SMSRepository smsRepository) {
        this.props = applicationProperties.getSms();
        this.smsRepository = smsRepository;
    }


    @Override
    public String send(SMSDTO dto) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("mobile_phone", dto.getPhone())
                .addFormDataPart("message", dto.getTemplate().withCode(dto.getCode()))
                .addFormDataPart("from", props.getFrom())
                .build();

        Request request = new Request.Builder()
                .url(props.getHost())
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                SMS sms = new SMS(dto.getUserId(), dto.getTemplate(), dto.getCode());
                smsRepository.save(sms);
            } else if (response.code() == 401 && !retried) {
                retried = true;
                updateToken();

                send(dto);
            } else {
                throw new RuntimeException("Request failed to send sms with status code: " + response.code());
            }

            return "sms sent";
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    private void updateToken() {
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("email", props.getUsername())
                .addFormDataPart("password", props.getPassword())
                .build();

        Request request = new Request.Builder()
                .url(props.getAuthUrl())
                .method("POST", body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                // Response body structure:
                /*
                  {
                    "message": "token_generated",
                    "data": {
                      "token": "auth-token"
                    },
                    "token_type": "bearer"
                  }
                 */

                String responseBody = response.body().string();
                String message = JsonPath.read(responseBody, "$.message");

                if ("token_generated".equals(message)) {
                    token = JsonPath.read(responseBody, "$.data.token");
                    retried = false;
                } else {
                    throw new RuntimeException("Request for 'token' failed in first attempt with code: " + response.code());
                }
            } else {
                throw new RuntimeException("Request for 'token' failed in first attempt with code: " + response.code());
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
