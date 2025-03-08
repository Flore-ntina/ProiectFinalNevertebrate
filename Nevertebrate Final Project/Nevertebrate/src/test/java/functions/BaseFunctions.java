package functions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;


public class BaseFunctions {

    public static void searchNoResults(WebDriver driver) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement searchBar = driver.findElement(By.name("s"));
            searchBar.sendKeys("fdgdg");
            searchBar.sendKeys(Keys.RETURN); 

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//section[@id='content']//h4")));

        WebElement contentSection = driver.findElement(By.xpath("//section[@id='content']"));
        String h4Text = contentSection.findElement(By.tagName("h4")).getText();
        String pText = contentSection.findElement(By.tagName("p")).getText();
        String liText1 = contentSection.findElements(By.tagName("li")).get(0).getText();
        String liText2 = contentSection.findElements(By.tagName("li")).get(1).getText();

        assertEquals("NU S-A GASIT NICIUN PRODUS RELEVANT.", h4Text);
        assertEquals("Te rugam sa cauti din nou ce doresti:", pText);
        assertEquals("- asigura-te ca ai scris corect", liText1);
        assertEquals("- incearca sa folosesti cautari mai scurte sau mai generale", liText2);

        Dimension size = contentSection.getSize();
        assertEquals("Expected box width to be", 848, size.getWidth());
        assertEquals("Expected box width to be", 253, size.getHeight());
    
        String backgroundColor = contentSection.getCssValue("background-color");
        assertEquals("Expected background color to be", "rgba(166, 191, 37, 1)", backgroundColor);
    }

    public static void searchProductResults(WebDriver driver) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement searchBar = driver.findElement(By.name("s"));
            searchBar.clear();
            searchBar.sendKeys("melci");
            searchBar.sendKeys(Keys.RETURN); 

        WebElement resultElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".heading-counter")));
        String actualText = resultElement.getText();
        String expectedText = "Sunt 59 produse."; 
        assertEquals( "Search result message does not match!", expectedText, actualText );

        WebElement veziElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[contains(text(), 'Vezi')]")));
        assertTrue("'VEZI' is not displayed!", veziElement.isDisplayed());

        WebElement sortElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[contains(text(), 'Sorteaza dupa')]")));
        assertTrue("'SORTEAZA DUPA' is not displayed!", sortElement.isDisplayed());

        List<WebElement> productTitles = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("#js-product-list .product-title a")));
    
        assertFalse("No products found!", productTitles.isEmpty());

        String firstProductTitle = productTitles.get(0).getText().toLowerCase();
        assertTrue("First product does not contain 'melci'!", firstProductTitle.contains("melci"));
    }

    public static void createNewWishlist(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement newWishlistModal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".wishlist-modal.modal.fade.show")));
        assertTrue("Create Wishlist modal did not appear!", newWishlistModal.isDisplayed());

        WebElement modalTitleNewElement = newWishlistModal.findElement(By.cssSelector(".modal-title"));
        assertEquals("CREEAZA O LISTA DE DORINTE", modalTitleNewElement.getText());

        WebElement wishlistNameInput = newWishlistModal.findElement(By.cssSelector("#input2"));
        wishlistNameInput.sendKeys("Test Wishlist");

        WebElement createButton = newWishlistModal.findElement(By.cssSelector(".btn.btn-primary"));
        createButton.click();

        wait.until(ExpectedConditions.invisibilityOf(newWishlistModal));

        WebElement successAddMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".wishlist-toast.success")));
        assertTrue("Success message did not appear!", successAddMessage.isDisplayed());  
        WebElement successMessageText = successAddMessage.findElement(By.cssSelector(".wishlist-toast-text"));
        assertEquals("Lista a fost corect creata.", successMessageText.getText().trim());

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//p[contains(text(), 'Lista a fost corect creata.')]")));
    }

    public static void deleteWhishlist(WebDriver driver, int iteration){

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement wishlistContainer = driver.findElement((By.cssSelector(".wishlist-container")));
        assertTrue("Wishlist container did not appear!", wishlistContainer.isDisplayed());  

        List<WebElement> wishlistItems = wishlistContainer.findElements(By.xpath("//ul[@class='wishlist-list']/li[contains(@class, 'wishlist-list-item')]"));

        WebElement wishlistElement = wishlistItems.get(iteration).findElement(By.cssSelector(".wishlist-list-item-title"));
        String wishlistName = wishlistElement.getText().trim();
        assertTrue("Wishlist name does not contain 'Test Wishlist'!", wishlistName.contains("Test Wishlist"));

        WebElement trashIconElement = wishlistItems.get(iteration).findElement(By.xpath(".//button[last()]"));
        trashIconElement.click();

        WebElement deleteWishlistModal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".wishlist-modal.modal.fade.show")));
        assertTrue("Create Wishlist modal did not appear!", deleteWishlistModal.isDisplayed());

        WebElement modalTitleNewElement = deleteWishlistModal.findElement(By.cssSelector(".modal-title"));
        assertEquals("STERGE LISTA DE DORINTE", modalTitleNewElement.getText());

        WebElement cancelButton = deleteWishlistModal.findElement(By.cssSelector(".modal-cancel.btn.btn-secondary"));
        cancelButton.click();

        wait.until(ExpectedConditions.invisibilityOf(deleteWishlistModal));
        trashIconElement.click();

        deleteWishlistModal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".wishlist-modal.modal.fade.show")));

        WebElement deleteButton = deleteWishlistModal.findElement(By.cssSelector(".btn.btn-primary"));
        deleteButton.click();

        WebElement successDeleteMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".wishlist-toast.success")));
        assertTrue("Success message did not appear!", successDeleteMessage.isDisplayed());

        WebElement successMessageText = successDeleteMessage.findElement(By.cssSelector(".wishlist-toast-text"));
        assertEquals("Lista a fost stearsa", successMessageText.getText().trim());
    }

    public static void addToCart (WebDriver driver, String [] product){
       
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        WebElement cartModal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("blockcart-modal")));
        assertTrue("Cart modal did not appear!", cartModal.isDisplayed());
            
        WebElement productName = cartModal.findElement(By.cssSelector(".product-name"));
        assertEquals(product[0], productName.getText().trim());
        
        WebElement productCountElement = cartModal.findElement(By.cssSelector(".cart-products-count"));
        assertEquals("Product count verified.", product[1], productCountElement.getText().trim());
            
        WebElement priceElement = cartModal.findElement(By.xpath("//p/strong[contains(text(),'Pret')]/following-sibling::span"));
        assertEquals(product[2], priceElement.getText().trim());
        
        WebElement quantityElement = cartModal.findElement(By.xpath("//p/strong[contains(text(),'Cantitate')]/following-sibling::span"));
        assertEquals(product[3], quantityElement.getText().trim());
        
        WebElement totalProductsElement = cartModal.findElement(By.xpath("//p/strong[contains(text(),'Cost total produse')]/following-sibling::span"));
        assertEquals(product[4], totalProductsElement.getText().trim());
        
        WebElement deliveryCostElement = cartModal.findElement(By.xpath("//p/strong[contains(text(),'Cost total livrare')]/following-sibling::span"));
        assertEquals(product[5], deliveryCostElement.getText().trim());
        
        WebElement tvaElement = cartModal.findElement(By.xpath("//p/strong[contains(text(),'TVA inclus')]/following-sibling::span"));
        assertEquals(product[6], tvaElement.getText().trim());
        
        WebElement totalElement = cartModal.findElement(By.xpath("//p/strong[contains(text(),'Total')]/following-sibling::span"));
        assertEquals(product[7], totalElement.getText().trim());
        
        WebElement continueShoppingButton = cartModal.findElement(By.cssSelector(".cart-content-btn .btn_skine-one"));
        continueShoppingButton.click();
            
        wait.until(ExpectedConditions.invisibilityOf(cartModal));
        
        WebElement cartProductCountElement = driver.findElement(By.cssSelector(".cart-products-count"));
        assertEquals(product[3], cartProductCountElement.getText().trim());
    }

    public static boolean checkCartModalIsOpened(WebDriver driver){

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));            

        boolean isCartModalOpened;
        try {
            WebElement cartModal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("blockcart-modal")));
            isCartModalOpened = cartModal.isDisplayed();
        } catch (TimeoutException e) {
            isCartModalOpened = false; // The modal did not appear
        }

        return isCartModalOpened;
    }

    public static void verifyCustomerBlockInCheckout(WebDriver driver){

        WebElement customerBlock = driver.findElement(By.cssSelector(".block-onepagecheckout.block-customer"));

        WebElement accountSection = customerBlock.findElement(By.className("title-heading"));
        assertEquals("Contul tau", accountSection.getText().trim());

        WebElement identityInfo = customerBlock.findElement(By.cssSelector(".identity"));
        assertTrue("User identity info is missing!", identityInfo.isDisplayed());
        assertTrue("User is not logged in!", identityInfo.getText().contains("Conectat ca"));

        WebElement logoutLink = customerBlock.findElement(By.xpath("//a[contains(text(), 'Deconectați-vă')]"));
        assertTrue("Logout link is missing!", logoutLink.isDisplayed());
    }

    public static void fillAddressInformationInCheckout(WebDriver driver){
        WebElement addressBlock = driver.findElement(By.cssSelector(".block-onepagecheckout.block-address"));

        WebElement firstName = addressBlock.findElement(By.id("shipping_address_firstname"));
        assertEquals("Test", firstName.getAttribute("value").trim());

        WebElement lastName = addressBlock.findElement(By.id("shipping_address_lastname"));
        assertEquals("User", lastName.getAttribute("value").trim());

        WebElement address = addressBlock.findElement(By.id("shipping_address_address1"));
        address.sendKeys("Street 1, number 123");

        WebElement city = addressBlock.findElement(By.id("shipping_address_city"));
        city.sendKeys("Bucharest");

        WebElement stateDropdown = addressBlock.findElement(By.id("shipping_address_id_state"));
        Select stateSelect = new Select(stateDropdown);
        stateSelect.selectByVisibleText("Bucuresti");

        WebElement zipCode = addressBlock.findElement(By.id("shipping_address_postal_code"));
        zipCode.clear();
        zipCode.sendKeys("010101");

        WebElement countryDropdown = addressBlock.findElement(By.id("shipping_address_id_country"));
        Select countrySelect = new Select(countryDropdown);
        String selectedCountry = countrySelect.getFirstSelectedOption().getText().trim();
        assertEquals("România", selectedCountry);
    }

    public static void verifyDeliveryMethodsInCheckout(WebDriver driver) { 
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement deliveryBlock = driver.findElement(By.cssSelector(".block-onepagecheckout.block-shipping"));

        WebElement deliveryTitle = deliveryBlock.findElement(By.className("title-heading"));
        assertEquals("Metoda de livrare", deliveryTitle.getText().trim());

        WebElement dpdCourierOption = wait.until(ExpectedConditions.presenceOfElementLocated((By.cssSelector("label[for='delivery_option_1516']"))));
        assertTrue("DPD Courier option is missing!", dpdCourierOption.isDisplayed());
        assertTrue("DPD Courier text is incorrect!", dpdCourierOption.getText().contains("DPD Courier"));
        assertTrue("DPD Courier price is incorrect!", dpdCourierOption.getText().contains("22,00 lei"));
        assertTrue("DPD Courier delivery time is missing!", dpdCourierOption.getText().contains("Livrare in 24-72h!"));

        WebElement fanCourierOption = driver.findElement(By.cssSelector("label[for='delivery_option_1515']"));
        assertTrue("FAN Courier option is missing!", fanCourierOption.isDisplayed());
        assertTrue("FAN Courier text is incorrect!", fanCourierOption.getText().contains("FAN Courier"));
        assertTrue("FAN Courier price is incorrect!", fanCourierOption.getText().contains("22,00 lei"));
        assertTrue("FAN Courier delivery time is missing!", fanCourierOption.getText().contains("Livrare in 24-72h"));

        WebElement pickupOption = driver.findElement(By.cssSelector("label[for='delivery_option_220']"));
        assertTrue("Preluare din magazin option is missing!", pickupOption.isDisplayed());
        assertTrue("Preluare din magazin text is incorrect!", pickupOption.getText().contains("Preluare din magazin"));
        assertTrue("Preluare din magazin price is incorrect!", pickupOption.getText().contains("Gratuit"));
        assertTrue("Preluare din magazin reservation info is missing!", pickupOption.getText().contains("Produsele se rezerva pentru 3 zile lucratoare"));

}

    public static void verifyPaymentMethodsInCheckout(WebDriver driver) {
        WebElement paymentBlock = driver.findElement(By.cssSelector(".block-onepagecheckout.block-payment"));

        WebElement paymentTitle = paymentBlock.findElement(By.className("title-heading"));
        assertEquals("Metoda de plata", paymentTitle.getText().trim());

        WebElement transferBancarOption = paymentBlock.findElement(By.cssSelector("label[for='payment-option-1']"));
        assertTrue("Transfer Bancar option is missing!", transferBancarOption.isDisplayed());
        assertTrue("Transfer Bancar text is incorrect!", transferBancarOption.getText().contains("Platiti prin transfer bancar"));

        WebElement cashOnDeliveryOption = paymentBlock.findElement(By.cssSelector("label[for='payment-option-2']"));
        assertTrue("Plata la livrare option is missing!", cashOnDeliveryOption.isDisplayed());
        assertTrue("Plata la livrare text is incorrect!", cashOnDeliveryOption.getText().contains("Plata la livrare"));

        WebElement onlinePaymentOption = paymentBlock.findElement(By.cssSelector("label[for='payment-option-3']"));
        assertTrue("Plata online prin EuPlatesc.ro option is missing!", onlinePaymentOption.isDisplayed());
        assertTrue("Plata online text is incorrect!", onlinePaymentOption.getText().contains("Plata online prin EuPlatesc.ro"));

    }

    public static void verifyCartSectionInCheckout(WebDriver driver) {
        WebElement cartBlock = driver.findElement(By.cssSelector(".block-onepagecheckout.block-shopping-cart"));

        WebElement cartTitle = cartBlock.findElement(By.className("title-heading"));
        assertEquals("Cos cumparaturi", cartTitle.getText().trim());

        WebElement cartItem = cartBlock.findElement(By.cssSelector(".cart-item"));
        assertTrue("Cart item is missing!", cartItem.isDisplayed());

        WebElement productName = cartItem.findElement(By.cssSelector(".product-line-info a.label"));
        assertEquals("Melci Turbo brunneus", productName.getText().trim());

        WebElement productQuantity = cartItem.findElement(By.cssSelector(".js-cart-line-product-quantity"));
        assertEquals("1", productQuantity.getAttribute("value").trim());

        WebElement subtotalPrice = cartBlock.findElement(By.cssSelector("#cart-subtotal-products .value"));
        assertEquals("25,83 lei", subtotalPrice.getText().trim());

        WebElement totalPrice = cartBlock.findElement(By.cssSelector(".cart-summary-line.cart-total .value"));
        assertEquals("47,83 lei", totalPrice.getText().trim());
    }

    public static void verifyMessageSectionInCheckout(WebDriver driver) {  
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement messageBlock = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".block-onepagecheckout.block-comment")));

        WebElement messageTitle = messageBlock.findElement(By.className("title-heading"));
        assertEquals("Mesaj comanda", messageTitle.getText().trim());

        WebElement messageTextarea = messageBlock.findElement(By.id("delivery_message"));
        assertTrue("Message textarea is missing!", messageTextarea.isDisplayed());
    }

}
