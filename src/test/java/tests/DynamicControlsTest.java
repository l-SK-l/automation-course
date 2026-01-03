package tests;

import base.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pages.DynamicControlsPage;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class DynamicControlsTest extends BaseTest {
    private DynamicControlsPage controlsPage;

    @BeforeEach
    public void setupPage() {
        controlsPage = new DynamicControlsPage(page);
        page.navigate("https://the-internet.herokuapp.com/dynamic_controls");
    }

    @Test
    public void testCheckboxRemoval() {
        controlsPage.clickRemoveButton();
        assertFalse(controlsPage.isCheckboxVisible());
    }
}
