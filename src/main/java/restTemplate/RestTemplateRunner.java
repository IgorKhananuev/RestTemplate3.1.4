package restTemplate;


import entity.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//Для работы предоставляется API по URL - http://91.241.64.178:7081/api/users
//
//        Ваша задача: Последовательно выполнить следующие операции и получить код для проверки на платформе:
//
//        Получить список всех пользователей
//        Когда вы получите ответ на свой первый запрос, вы должны сохранить свой session id, который получен через cookie. Вы получите его в заголовке ответа set-cookie. По скольку все действия происходят в рамках одной сессии, все дальнейшие запросы должны использовать полученный session id ( необходимо использовать заголовок в последующих запросах )
//        Сохранить пользователя с id = 3, name = James, lastName = Brown, age = на ваш выбор. В случае успеха вы получите первую часть кода.
//        Изменить пользователя с id = 3. Необходимо поменять name на Thomas, а lastName на Shelby. В случае успеха вы получите еще одну часть кода.
//        Удалить пользователя с id = 3. В случае успеха вы получите последнюю часть кода.
//        В результате выполненных операций вы должны получить итоговый код, сконкатенировав все его части. Количество символов в коде = 18.

@SpringBootApplication
public class RestTemplateRunner {

    private static final String GET_POST_PUT_Url = "http://91.241.64.178:7081/api/users";
    private static final String DELETE_Url = "http://91.241.64.178:7081/api/users/{id}";

    public static void main(String[] args) {
        SpringApplication.run(RestTemplateRunner.class, args);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entityString = new HttpEntity<>(httpHeaders);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> responseEntity = restTemplate.exchange(GET_POST_PUT_Url,
                HttpMethod.GET, entityString, String.class);

        String cookies = responseEntity.getHeaders().getFirst("Set-Cookie");

        System.out.println("Cookies " + cookies);
        System.out.println("Response body " + responseEntity.getBody());

        restTemplate.getInterceptors().add(new ClientHttpRequestInterceptor() {
            @Override
            public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                                ClientHttpRequestExecution execution) throws IOException {
                request.getHeaders().set("Cookie", cookies);
                return execution.execute(request, body);
            }
        });

        User newUser = new User(3L, "James", "Brown", (byte) 22);
        HttpEntity<User> postUser = new HttpEntity<>(newUser, httpHeaders);
        ResponseEntity<String> resultPOST = restTemplate.exchange(GET_POST_PUT_Url, HttpMethod.POST, postUser, String.class);
        System.out.println("Save result " + resultPOST.getBody());

        User changedUser = new User(3L, "Thomas", "Shelby", (byte) 22);
        HttpEntity<User> putUser = new HttpEntity<>(changedUser, httpHeaders);
        ResponseEntity<String> resultPut = restTemplate.exchange(GET_POST_PUT_Url, HttpMethod.PUT, putUser, String.class);
        System.out.println("Update result " + resultPut.getBody());

        Map<String, Long> parameters = new HashMap<>();
        parameters.put("id", 3L);
        ResponseEntity<String> resultDELETE = restTemplate.exchange(DELETE_Url, HttpMethod.DELETE,
                null, String.class, parameters);
        System.out.println("Delete result " + resultDELETE.getBody());

        System.out.println(resultPOST.getBody() + resultPut.getBody() + resultDELETE.getBody());
    }
}


