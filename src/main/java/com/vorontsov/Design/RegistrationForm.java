package com.vorontsov.Design;

import com.vorontsov.MyUI;
import com.vorontsov.Services.*;

import com.vaadin.server.*;
import com.vaadin.ui.GridLayout;

import com.vaadin.ui.*;
import org.vaadin.activelink.ActiveLink;

public class RegistrationForm extends GridLayout {
    private SigninForm signinForm;
    private MainForm mainForm;
    private MyUI myUI;
    VerticalLayout formLayout = new VerticalLayout();
    private Panel mainPanel = new Panel();
    private HorizontalLayout infoLayout = new HorizontalLayout();

    private Label registerLabel = new Label("Registration");
    private TextField loginTextField = new TextField("Username");
    private PasswordField passwordPasswordField = new PasswordField("Password");
    private PasswordField repeatPasswordPasswordField = new PasswordField("Repeat password");
    private Button createAccountButton = new Button("Create account");
    private Image cbImage = new Image();
    private Label infoLabel = new Label("Есть аккаунт?");
    private ActiveLink gotoSignIn = new ActiveLink();

    public RegistrationForm(MyUI myUI) {
        this.myUI = myUI;
        cbImage.setSource(new ExternalResource("http://x-lines.ru/letters/i/cyrillicscript/0086/5484ed/20/0/epwsh3mpcrorramucw.png"));
        registerLabel.setStyleName("colored bold");
        loginTextField.setInputPrompt("6-12 symbols");
        loginTextField.setIcon(FontAwesome.USER);
        loginTextField.setStyleName("inline-icon");
        loginTextField.setMaxLength(12);
        passwordPasswordField.setInputPrompt("4-12 symbols");
        passwordPasswordField.setIcon(FontAwesome.LOCK);
        passwordPasswordField.setStyleName("inline-icon");
        passwordPasswordField.setMaxLength(12);
        repeatPasswordPasswordField.setInputPrompt("6-12 symbols");
        repeatPasswordPasswordField.setIcon(FontAwesome.LOCK);
        repeatPasswordPasswordField.setStyleName("inline-icon");
        repeatPasswordPasswordField.setMaxLength(12);
        createAccountButton.setStyleName("primary");
        gotoSignIn.setCaption("Вход");
        gotoSignIn.addListener(new ActiveLink.LinkActivatedListener() {
            @Override
            public void linkActivated(ActiveLink.LinkActivatedEvent linkActivatedEvent) {
                signinForm = new SigninForm(myUI);
                myUI.setContent(signinForm);
            }
        });
        createAccountButton.addClickListener(e -> {
            if(loginTextField.getValue().length() < 4) {
                Notification.show("Ошибка", "Логин должен содержать от 4 символов.", Notification.Type.WARNING_MESSAGE);
                return;
            }
            if(MySQLService.isUserExists(loginTextField.getValue())) {
                Notification.show("Ошибка", "Такой пользователь уже зарегистрирован", Notification.Type.WARNING_MESSAGE);
                return;
            }
            if(passwordPasswordField.getValue().length() < 4) {
                Notification.show("Ошибка", "Пароль должен содержать от 4 символов.", Notification.Type.WARNING_MESSAGE);
                return;
            }
            if(!(passwordPasswordField.getValue().equals(repeatPasswordPasswordField.getValue()))) {
                Notification.show("Ошибка", "Пароли не совпадают", Notification.Type.WARNING_MESSAGE);
                return;
            }
            MySQLService.createUser(loginTextField.getValue(), passwordPasswordField.getValue());
            mainForm = new MainForm(myUI);
            myUI.setContent(mainForm);
            Notification.show("Успешно", "Аккаунт " + loginTextField.getValue() + " успешно зарегистрирован", Notification.Type.HUMANIZED_MESSAGE);
        });

        formLayout.addComponents(cbImage, registerLabel, loginTextField, passwordPasswordField, repeatPasswordPasswordField, createAccountButton, infoLayout);
        formLayout.setComponentAlignment(infoLayout, Alignment.MIDDLE_CENTER);
        formLayout.setComponentAlignment(cbImage, Alignment.MIDDLE_CENTER);
        formLayout.setComponentAlignment(createAccountButton, Alignment.MIDDLE_CENTER);
        formLayout.setMargin(true);
        formLayout.setSpacing(true);
        infoLayout.addComponents(infoLabel, gotoSignIn);
        infoLayout.setSpacing(true);

        mainPanel.setWidth(null);
        mainPanel.setContent(formLayout);

        addComponent(mainPanel);
        setComponentAlignment(mainPanel, Alignment.MIDDLE_CENTER);
        setRows(2);
        setSizeFull();
    }
}
