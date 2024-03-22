package scrolls.elder.model.person;
import java.util.Optional;
import java.util.Set;

import scrolls.elder.commons.util.ToStringBuilder;
import scrolls.elder.model.tag.Tag;

/**
 * Represents a Befriendee in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Befriendee extends Person {
    public Befriendee(Name name, Phone phone, Email email, Address address, Set<Tag> tags,
                      Optional<Name> pairedWithName, Optional<Integer> pairedWithID) {
        super(name, phone, email, address, tags, new Role("befriendee"), pairedWithName, pairedWithID);
    }

    @Override
    public boolean isVolunteer() {
        return false;
    }

    @Override
    public Role getRole() {
        return this.role;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Befriendee)) {
            return false;
        }

        Befriendee otherBefriendee = (Befriendee) other;
        return name.equals(otherBefriendee.name)
                && phone.equals(otherBefriendee.phone)
                && email.equals(otherBefriendee.email)
                && address.equals(otherBefriendee.address)
                && tags.equals(otherBefriendee.tags)
                && pairedWithName.equals(otherBefriendee.pairedWithName)
                && pairedWithID.equals(otherBefriendee.pairedWithID);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("tags", tags)
                .add("role", role)
                .add("pairedWithName", pairedWithName.orElse(Name.getNone()))
                .add("pairedWithID", pairedWithID.orElse(-1))
                .toString();
    }
}
