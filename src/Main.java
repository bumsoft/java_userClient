import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Main {
    String sessionCookie;

    public void register()
    {
        try {
            // URL 설정
            URL url = new URL("http://localhost:8080/register");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // HTTP 설정
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            //쉽게 json으로 보내기위해 jackson 사용함
            RegisterDto registerDto = new RegisterDto("id1", "pw1");

            // Jackson ObjectMapper를 사용해 객체를 JSON으로
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonInputString = objectMapper.writeValueAsString(registerDto);

            // 데이터 전송
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 응답 코드 확인
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // 응답 본문 읽기
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("가입 성공");
            } else {
                System.out.println("가입 실패");
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void login()
    {
        try {
            // URL 설정
            URL url = new URL("http://localhost:8080/login");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // HTTP 설정
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);

            // 자바 객체 생성
            String username = "id1";
            String password = "pw1";

            String urlParameters = "username=" + URLEncoder.encode(username, "UTF-8") +
                    "&password=" + URLEncoder.encode(password, "UTF-8");

            // username=id1&password=pw1 형식으로 보냄.

            // 데이터 전송
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = urlParameters.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 응답 코드 확인
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode); //성공:200 실패:401로 서버에서 설정해둠

            // 응답 본문 읽기
            if (responseCode == HttpURLConnection.HTTP_OK) { //200
                System.out.println("로그인 성공");

                //로그인 성공시 서버에서 세션 쿠키가 반환됨. 이걸 보관하며, 모든 요청에 이걸 포함에서 보내야 로그인된 사용자라는걸 서버가 알 수 있음.
                String setCookieHeader = connection.getHeaderField("Set-Cookie");
                if (setCookieHeader != null) {
                      sessionCookie = setCookieHeader.split(";")[0]; // "JSESSIONID=..." 형태에서 JSESSIONID만 추출
                    System.out.println("Session Cookie: " + sessionCookie);
                }
                else {
                    System.out.println("setCookieHeader is null");
                }
            } else { //401
                System.out.println("로그인 실패");
            }
            System.out.println("=====================================");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    private String readResponse(HttpURLConnection connection) throws IOException
//    {
//        StringBuilder response = new StringBuilder();
//        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
//            String line;
//            while ((line = in.readLine()) != null) {
//                response.append(line);
//            }
//        }
//        return response.toString();
//    }

    //로그인 후, 유지가 되는지 확인하기 위한 메서드(여기서 확인x, 서버에서 확인하기). //아무 요청이나 보내고, 서버로그에 principal.getName()하게 해서 확인해주면 됨.
    public void test() {
        if (sessionCookie == null) {
            System.out.println("로그인하지 않았습니다. 보호된 리소스에 접근하려면 먼저 로그인해야 합니다.");
            return;
        }

        try {
            URL url = new URL("http://localhost:8080/test");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // HTTP GET 요청 설정
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Cookie", sessionCookie); // 세션 쿠키를 헤더에 포함

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.register();
        main.login();
        main.test();
    }
}


