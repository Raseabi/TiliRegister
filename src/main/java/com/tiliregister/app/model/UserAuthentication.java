package com.tiliregister.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_authentication")
public class UserAuthentication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonSerialize(using = UserSerializer.class)
    private User user;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Column(name = "password_status", nullable = false)
    private String passwordStatus;

    @Column(name = "password_date_modified", nullable = false)
    private LocalDateTime passwordDateModified;

    @Column(name = "login_attempts", nullable = false)
    private int loginAttempts;

    @Column(name = "reset_password_attempts", nullable = false)
    private int resetPasswordAttempts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordStatus() {
        return passwordStatus;
    }

    public void setPasswordStatus(String passwordStatus) {
        this.passwordStatus = passwordStatus;
    }

    public LocalDateTime getPasswordDateModified() {
        return passwordDateModified;
    }

    public void setPasswordDateModified(LocalDateTime passwordDateModified) {
        this.passwordDateModified = passwordDateModified;
    }

    public int getLoginAttempts() {
        return loginAttempts;
    }

    public void setLoginAttempts(int loginAttempts) {
        this.loginAttempts = loginAttempts;
    }

    public int getResetPasswordAttempts() {
        return resetPasswordAttempts;
    }

    public void setResetPasswordAttempts(int resetPasswordAttempts) {
        this.resetPasswordAttempts = resetPasswordAttempts;
    }

    @Override
    public String toString() {
        return "UserAuthentication{" +
                "id=" + id +
                ", user=" + user +
                ", password='" + password + '\'' +
                ", passwordStatus='" + passwordStatus + '\'' +
                ", passwordDateModified=" + passwordDateModified +
                ", loginAttempts=" + loginAttempts +
                ", resetPasswordAttempts=" + resetPasswordAttempts +
                '}';
    }
}
