class RegisterDto {
    private String username;
    private String password;

    // 생성자
    public RegisterDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter와 Setter (생략 가능)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}