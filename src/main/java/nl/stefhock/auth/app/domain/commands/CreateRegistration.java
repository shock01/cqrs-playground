package nl.stefhock.auth.app.domain.commands;

import nl.stefhock.auth.cqrs.application.Command;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by hocks on 5-7-2017.
 */
public class CreateRegistration extends Command {

    private final String uuid;
    @Email
    private final String email;
    @NotNull
    @Size(min = 6)
    private final String password;
    private final String source;

    public CreateRegistration(String uuid, String email, String password, String source) {
        this.uuid = uuid;
        this.email = email;
        this.password = password;
        this.source = source;

    }

    public String getUuid() {
        return uuid;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getSource() {
        return source;
    }
}
