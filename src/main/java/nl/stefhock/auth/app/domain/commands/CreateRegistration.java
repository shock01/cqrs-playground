package nl.stefhock.auth.app.domain.commands;

import com.fasterxml.jackson.annotation.JsonProperty;
import nl.stefhock.auth.cqrs.application.Command;

import javax.validation.constraints.NotNull;

/**
 * Created by hocks on 5-7-2017.
 */
public class CreateRegistration extends Command {

    private final RegistrationInfo registration;
    private final String uuid;

    // @// FIXME: 5-7-2017 a command should validate it's input
    // @// FIXME: 5-7-2017 a application create needs to be broadcasted
    public CreateRegistration(final String uuid,
                              final RegistrationInfo registration) {
        this.uuid = uuid;
        this.registration = registration;
    }

    public String getUuid() {
        return uuid;
    }

    public RegistrationInfo getRegistration() {
        return registration;
    }

    // @todo use a builder here!
    public static class RegistrationInfo {
        @NotNull(message = "email required")
        private final String email;

        private final String password;

        private final String source;

        /**
         * password is not required eg when a registration is done using a
         * social network like Google
         *
         * @param email
         * @param password
         * @todo registration source should be added
         */
        public RegistrationInfo(final @JsonProperty("email") String email,
                                final @JsonProperty("password") String password,
                                final @JsonProperty("source") String source) {
            this.email = email;
            this.password = password;
            this.source = source;
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
}
