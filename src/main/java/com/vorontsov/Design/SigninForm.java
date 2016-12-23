package com.vorontsov.Design;

import com.vaadin.server.*;
import com.vaadin.ui.*;
import com.vorontsov.Design.RegistrationForm;
import com.vorontsov.MyUI;
import com.vorontsov.Services.MySQLService;
import org.vaadin.activelink.ActiveLink;

public class SigninForm extends GridLayout {
    private RegistrationForm registrationForm;
    private MainForm mainForm;
    private MyUI myUI;
    private VerticalLayout formLayout = new VerticalLayout();
    private HorizontalLayout activitiesLayout = new HorizontalLayout();
    private HorizontalLayout infoLayout = new HorizontalLayout();
    private Panel mainPanel = new Panel();

    private Label welcomeLabel = new Label("WELCOME");
    private TextField loginTextField = new TextField("Username");
    private PasswordField passwordPasswordFiled = new PasswordField("Password");
    private Button signinButton = new Button("Sign in");
    private Image cbImage = new Image();
    private Label infoLabel = new Label("Еще нет аккаунта?");
    private ActiveLink gotoRegisterLink = new ActiveLink();

    public SigninForm(MyUI myUI) {
        this.myUI = myUI;
        cbImage.setSource(new ExternalResource("http://x-lines.ru/letters/i/cyrillicscript/0086/5484ed/20/0/epwsh3mpcrorramucw.png"));
        welcomeLabel.setStyleName("colored bold");
        loginTextField.setInputPrompt("Username..");
        loginTextField.setIcon(FontAwesome.USER);
        loginTextField.setStyleName("inline-icon");
        loginTextField.setMaxLength(10);
        passwordPasswordFiled.setInputPrompt("Password..");
        passwordPasswordFiled.setIcon(FontAwesome.LOCK);
        passwordPasswordFiled.setStyleName("inline-icon");
        passwordPasswordFiled.setMaxLength(10);
        signinButton.setStyleName("primary");
        gotoRegisterLink.setCaption("Зарегистрироваться");
        gotoRegisterLink.addListener(new ActiveLink.LinkActivatedListener() {
            @Override
            public void linkActivated(ActiveLink.LinkActivatedEvent linkActivatedEvent) {
                registrationForm = new RegistrationForm(myUI);
                myUI.setContent(registrationForm);
            }
        });
        signinButton.addClickListener(e -> {
            if(MySQLService.tryAuthorize(loginTextField.getValue(), passwordPasswordFiled.getValue())) {
                mainForm = new MainForm(myUI);
                myUI.setContent(mainForm);
            }
            else Notification.show("Ошибка", "Неверный логин и/или пароль", Notification.Type.ERROR_MESSAGE);
        });

        formLayout.addComponents(cbImage, welcomeLabel, activitiesLayout, infoLayout);
        formLayout.setComponentAlignment(cbImage, Alignment.MIDDLE_CENTER);
        formLayout.setMargin(true);
        formLayout.setSpacing(true);
        activitiesLayout.addComponents(loginTextField, passwordPasswordFiled, signinButton);
        activitiesLayout.setComponentAlignment(signinButton, Alignment.BOTTOM_CENTER);
        activitiesLayout.setSpacing(true);
        infoLayout.addComponents(infoLabel, gotoRegisterLink);
        infoLayout.setSpacing(true);

        mainPanel.setWidth(null);
        mainPanel.setContent(formLayout);

        addComponent(mainPanel);
        setComponentAlignment(mainPanel, Alignment.MIDDLE_CENTER);
        setRows(2);
        setSizeFull();
    }
}
