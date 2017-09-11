package nl.stefhock.auth.app.application.registrations.queries;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.util.Date;

/**
 * Created by hocks on 17-8-2017.
 */
public class RegistrationView implements DataSerializable {

    @JsonProperty
    private Date registrationDate;
    @JsonProperty
    private String email;
    @JsonProperty
    private String uuid;

    @SuppressWarnings("unused")
    public RegistrationView() {
    }

    public RegistrationView(String email, Date registrationDate, String uuid) {
        this.email = email;
        this.registrationDate = registrationDate;
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public String getEmail() {
        return email;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(uuid);
        out.writeUTF(email);
        out.writeLong(registrationDate.getTime());
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        uuid = in.readUTF();
        email = in.readUTF();
        registrationDate = new Date(in.readLong());
    }
}
