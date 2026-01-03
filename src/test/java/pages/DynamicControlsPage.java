package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

public class DynamicControlsPage {
    private final Page page;

    public DynamicControlsPage(Page page) {
        this.page = page;
    }

    public void clickRemoveButton() {
        page.locator("button:has-text('Remove')").click();
        page.locator("#loading").waitFor(new com.microsoft.playwright.Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
    }

    public boolean isCheckboxVisible() {
        try {
            return page.locator("#checkbox").isVisible();
        } catch (Exception e) {
            return false;
        }
    }
}
