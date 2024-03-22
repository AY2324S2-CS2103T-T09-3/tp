package scrolls.elder.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import scrolls.elder.commons.core.index.Index;
import scrolls.elder.logic.Messages;
import scrolls.elder.model.AddressBook;
import scrolls.elder.model.Model;
import scrolls.elder.model.ModelManager;
import scrolls.elder.model.UserPrefs;
import scrolls.elder.model.person.Person;
import scrolls.elder.model.person.Role;
import scrolls.elder.testutil.EditPersonDescriptorBuilder;
import scrolls.elder.testutil.PersonBuilder;
import scrolls.elder.testutil.TypicalIndexes;
import scrolls.elder.testutil.TypicalPersons;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditCommand.
 */
public class EditCommandTest {


    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Person editedPerson = new PersonBuilder(TypicalPersons.BENSON).build();
        EditCommand.EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        EditCommand editCommand = new EditCommand(TypicalIndexes.INDEX_SECOND_PERSON, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model model = new ModelManager(TypicalPersons.getTypicalAddressBook(), new UserPrefs());
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(expectedModel.getFilteredPersonList().get(1), editedPerson);

        CommandTestUtil.assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Model model = new ModelManager(TypicalPersons.getTypicalAddressBook(), new UserPrefs());
        Index indexSecondPerson = Index.fromOneBased(2);
        Person secondPerson = model.getFilteredVolunteerList().get(indexSecondPerson.getZeroBased());

        PersonBuilder personInList = new PersonBuilder(secondPerson);
        Person editedPerson =
                personInList.withName(CommandTestUtil.VALID_NAME_BOB).withPhone(CommandTestUtil.VALID_PHONE_BOB)
                        .withTags(CommandTestUtil.VALID_TAG_HUSBAND)
                        .withRole(CommandTestUtil.VALID_ROLE_VOLUNTEER).build();

        EditCommand.EditPersonDescriptor descriptor =
                new EditPersonDescriptorBuilder().withName(CommandTestUtil.VALID_NAME_BOB)
                        .withPhone(CommandTestUtil.VALID_PHONE_BOB).withTags(CommandTestUtil.VALID_TAG_HUSBAND)
                        .withRole(CommandTestUtil.VALID_ROLE_VOLUNTEER).build();
        EditCommand editCommand = new EditCommand(indexSecondPerson, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(
                expectedModel.getFilteredVolunteerList().get(indexSecondPerson.getZeroBased()), editedPerson);

        CommandTestUtil.assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        Model model = new ModelManager(TypicalPersons.getTypicalAddressBook(), new UserPrefs());

        EditCommand.EditPersonDescriptor epd = new EditCommand.EditPersonDescriptor();
        epd.setRole(new Role("volunteer"));
        EditCommand editCommand =
                new EditCommand(TypicalIndexes.INDEX_FIRST_PERSON, epd);
        Person editedPerson = model.getFilteredPersonList().get(TypicalIndexes.INDEX_FIRST_PERSON.getZeroBased());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        CommandTestUtil.assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        Model model = new ModelManager(TypicalPersons.getTypicalAddressBook(), new UserPrefs());

        CommandTestUtil.showPersonAtIndex(model, TypicalIndexes.INDEX_SECOND_PERSON);

        Person personInFilteredList =
                model.getFilteredPersonList().get(TypicalIndexes.INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personInFilteredList).withName(CommandTestUtil.VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(TypicalIndexes.INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withName(CommandTestUtil.VALID_NAME_BOB)
                        .withRole("volunteer").build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(expectedModel.getFilteredPersonList().get(1), editedPerson);

        CommandTestUtil.assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Model model = new ModelManager(TypicalPersons.getTypicalAddressBook(), new UserPrefs());

        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        EditCommand.EditPersonDescriptor descriptor =
                new EditPersonDescriptorBuilder().withName(CommandTestUtil.VALID_NAME_BOB)
                        .withRole("befriendee").build();
        EditCommand editCommand = new EditCommand(outOfBoundIndex, descriptor);

        CommandTestUtil.assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of address book
     */
    @Test
    public void execute_invalidPersonIndexFilteredList_failure() {
        Model model = new ModelManager(TypicalPersons.getTypicalAddressBook(), new UserPrefs());

        CommandTestUtil.showPersonAtIndex(model, TypicalIndexes.INDEX_FIRST_PERSON);
        Index outOfBoundIndex = TypicalIndexes.INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        EditCommand editCommand = new EditCommand(outOfBoundIndex,
                new EditPersonDescriptorBuilder().withName(CommandTestUtil.VALID_NAME_BOB)
                        .withRole("volunteer").build());

        CommandTestUtil.assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final EditCommand standardCommand =
                new EditCommand(TypicalIndexes.INDEX_FIRST_PERSON, CommandTestUtil.DESC_AMY_VOLUNTEER);

        // same values -> returns true
        EditCommand.EditPersonDescriptor copyDescriptor =
                new EditCommand.EditPersonDescriptor(CommandTestUtil.DESC_AMY_VOLUNTEER);
        EditCommand commandWithSameValues = new EditCommand(TypicalIndexes.INDEX_FIRST_PERSON, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(
                standardCommand.equals(new EditCommand(TypicalIndexes.INDEX_SECOND_PERSON,
                        CommandTestUtil.DESC_AMY_VOLUNTEER)));

        // different descriptor -> returns false
        assertFalse(
                standardCommand.equals(new EditCommand(TypicalIndexes.INDEX_FIRST_PERSON,
                        CommandTestUtil.DESC_BOB_BEFRIENDEE)));
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        EditCommand.EditPersonDescriptor editPersonDescriptor = new EditCommand.EditPersonDescriptor();
        EditCommand editCommand = new EditCommand(index, editPersonDescriptor);
        String expected = EditCommand.class.getCanonicalName() + "{index=" + index + ", editPersonDescriptor="
                + editPersonDescriptor + "}";
        assertEquals(expected, editCommand.toString());
    }

}
