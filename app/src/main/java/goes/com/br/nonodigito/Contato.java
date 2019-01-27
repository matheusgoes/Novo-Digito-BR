package goes.com.br.nonodigito;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by matheusgoes on 11/07/15.
 */
public class Contato implements Serializable, Parcelable {
    private static final long serialVersionUID = -7060210544600464481L;
    String id;
    String name;
    String oldNumber;
    String newNumber;
    int type;

    public Contato(String id, String name, String oldNumber, String newNumber, int type) {
        this.id = id;
        this.name = name;
        this.oldNumber = oldNumber;
        this.newNumber = newNumber;
        this.type = type;
    }
    public Contato() {
    }
    private Contato(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.oldNumber = in.readString();
        this.newNumber = in.readString();
        this.type = in.readInt();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOldNumber() {
        return oldNumber;
    }

    public void setOldNumber(String oldNumber) {
        this.oldNumber = oldNumber;
    }

    public String getNewNumber() {
        return newNumber;
    }

    public void setNewNumber(String newNumber) {
        this.newNumber = newNumber;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(oldNumber);
        dest.writeString(newNumber);
        dest.writeInt(type);
    }

    public static final Parcelable.Creator<Contato> CREATOR
            = new Parcelable.Creator<Contato>() {
        public Contato createFromParcel(Parcel in) {
            return new Contato(in);
        }

        public Contato[] newArray(int size) {
            return new Contato[size];
        }
    };


}
