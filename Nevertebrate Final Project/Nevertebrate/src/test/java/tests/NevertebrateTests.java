package tests;

import java.time.Duration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
import java.util.NoSuchElementException;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Timeouts;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import functions.BaseFunctions;

public class NevertebrateTests {
    WebDriver driver;

@Before
    public void openHomePage() {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(10000));
        driver.manage().window().maximize();

        driver.get("https://www.nevertebrate.ro");
        String title = driver.getTitle();
        assertEquals("Magazin de acvaristica si teraristica Nevertebrate.ro", title);  

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement rejectCookiesButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@class='cookiesplus-btn cookiesplus-reject']")));
        rejectCookiesButton.click();
        assertEquals("Magazin de acvaristica si teraristica Nevertebrate.ro", title);  
    }

@Test
public void checkSearchResults() {

    BaseFunctions.searchNoResults(driver);
    BaseFunctions.searchProductResults(driver);
    
}

@Test
public void registerNewUser() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    WebElement registerButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a [@title = 'Nu ai cont? Creeaza unul aici']")));
    registerButton.click();

    String expectedURL = "https://www.nevertebrate.ro/index.php?controller=registration";
    wait.until(ExpectedConditions.urlToBe(expectedURL));
    assertEquals("Registration page did not load correctly!", expectedURL, driver.getCurrentUrl());

    WebElement pageHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(text(), 'Creeaza un cont')]")));
    assertTrue("Register page header is missing!", pageHeader.isDisplayed());

    WebElement firstName = driver.findElement(By.id("field-firstname"));
    firstName.sendKeys("Test");

    WebElement lastName = driver.findElement(By.id("field-lastname"));
    lastName.sendKeys("User");

    WebElement email = driver.findElement(By.id("field-email"));
    String testEmail = "testuser" + System.currentTimeMillis() + "@mail.com"; 
    email.sendKeys(testEmail);//testuser1740@mail.com

    WebElement password = driver.findElement(By.id("field-password"));
    password.sendKeys("Test@1234");

    WebElement newsletterCheckbox = driver.findElement(By.cssSelector("input[name='newsletter']"));
    if (newsletterCheckbox.isSelected()) { 
        newsletterCheckbox.click();
    }
    assertFalse("Newsletter checkbox should be unchecked!", newsletterCheckbox.isSelected());

    WebElement saveButton = driver.findElement(By.xpath("//button[@type='submit' and contains(text(), 'Salveaza')]"));
    saveButton.click();
    
    expectedURL = "https://www.nevertebrate.ro/";
    wait.until(ExpectedConditions.urlToBe(expectedURL));
    assertEquals("Registration page did not load correctly!", expectedURL, driver.getCurrentUrl());
    assertEquals("Magazin de acvaristica si teraristica Nevertebrate.ro", driver.getTitle()); 

    WebElement logoutButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("logout")));
    assertTrue("Logout button is not displayed!", logoutButton.isDisplayed());
}

@Test
public void loginExistingUser() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    WebElement registerButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a [@title = 'Conecteaza-te la contul de client']")));
    registerButton.click();

    String expectedURL = "https://www.nevertebrate.ro/autentificare?back=my-account";
    wait.until(ExpectedConditions.urlToBe(expectedURL));
    assertEquals("Registration page did not load correctly!", expectedURL, driver.getCurrentUrl());

    WebElement pageHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(text(), 'Autentifica-te in contul tau')]")));
    assertTrue("Login page header is missing!", pageHeader.isDisplayed());

    WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("field-email")));
    emailField.sendKeys("testuser1740@mail.com"); 

    WebElement passwordField = driver.findElement(By.id("field-password"));
    passwordField.sendKeys("Test@1234");  

    WebElement loginButton = driver.findElement(By.id("submit-login"));
    loginButton.click();

    expectedURL = "https://www.nevertebrate.ro/contul-meu";
    wait.until(ExpectedConditions.urlToBe(expectedURL));
    assertEquals("Registration page did not load correctly!", expectedURL, driver.getCurrentUrl());

    WebElement logoutButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("logout")));
    assertTrue("Logout button is not displayed!", logoutButton.isDisplayed());

    WebElement myAccountContainer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("block_myaccount_infos")));
    assertTrue("My Account container did not appear!", myAccountContainer.isDisplayed());

    String[] accountOptions = {
        "contul-meu", "identitate", "returnari", "istoria-comenzilor", "nota-credit",
        "adrese", "reducere", "ps_emailalerts/account", "ws_productreviews/myreviews",
        "harta-site", "magazine"
    };

    for (String option : accountOptions) {
        WebElement accountLink = myAccountContainer.findElement(By.xpath("//a[contains(@href, '" + option + "')]"));
        assertTrue("Account option missing: " + option, accountLink.isDisplayed());
        System.out.println("Verified: " + accountLink.getText());
    }
}

@Test
public void logoutFromAccount() {

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    String[] accountOptions = {
        "identitate", "returnari", "istoria-comenzilor", "nota-credit",
        "adrese", "reducere", "ps_emailalerts/account", "ws_productreviews/myreviews",
        "harta-site", "magazine"
    };

    loginExistingUser();   
    String beforeLogoutUrl = driver.getCurrentUrl();

    WebElement logoutButton = driver.findElement(By.className("logout"));
    assertTrue("Logout button is not displayed!", logoutButton.isDisplayed());

    logoutButton.click();

    WebElement loginButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a[href*='contul-meu']")));
    assertTrue("Autentificare button is not displayed!", loginButton.isDisplayed());

    WebElement inregistrareButton = driver.findElement(By.cssSelector(".header_user_info a[href*='registration']"));
    assertTrue("Inregistrare button is not displayed!", inregistrareButton.isDisplayed());

    boolean isAccountUrl = false;

    for (String option : accountOptions) {
        if (beforeLogoutUrl.contains(option)) {
            isAccountUrl = true;
            break; // Exit loop once a match is found
        }
    }
    if (isAccountUrl){
        WebElement pageHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(text(), 'Autentifica-te in contul tau')]")));
        assertTrue("User was not redirected to correct page after Logout!", driver.getCurrentUrl().contains("autentificare"));
        assertTrue("Login page header is missing!", pageHeader.isDisplayed());
    }
    else{
        if (beforeLogoutUrl.contains("cos") || beforeLogoutUrl.contains( "contul-meu")) {
            assertEquals("User was not redirected to correct page after Logout!", "https://www.nevertebrate.ro/", driver.getCurrentUrl());
        }
        else assertEquals("User was not redirected to correct page after Logout!", beforeLogoutUrl, driver.getCurrentUrl());
    }    
}

@Test
public void addToFavoritesNotLoggedin() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    
    BaseFunctions.searchProductResults(driver);

    WebElement firstProductContainer = driver.findElement((By.cssSelector("#js-product-list .product-miniature")));
    WebElement wishlistButton = firstProductContainer.findElement(By.cssSelector(".wishlist-button-add"));
    wishlistButton.click();

    WebElement wishlistModal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".wishlist-modal.modal.fade.show")));
    assertTrue("Wishlist authentication popup did not appear!", wishlistModal.isDisplayed());

    WebElement modalTitle = wishlistModal.findElement(By.cssSelector(".modal-title"));
    assertEquals("AUTENTIFICARE", modalTitle.getText());

    WebElement modalMessage = wishlistModal.findElement(By.cssSelector(".modal-text"));
    assertEquals("Ai nevoie sa fii autentificat pentru a salva produsele in lista de dorinte.", modalMessage.getText());

    WebElement cancelButton = wishlistModal.findElement(By.cssSelector(".modal-cancel.btn.btn-secondary"));
    cancelButton.click();

    wait.until(ExpectedConditions.invisibilityOf(wishlistModal));
    wishlistButton.click();

    wishlistModal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".wishlist-modal.modal.fade.show")));

    WebElement authentificationButton = wishlistModal.findElement(By.cssSelector(".btn.btn-primary"));
    authentificationButton.click();

    assertTrue("Did not navigate to login page!", driver.getCurrentUrl().contains("/autentificare"));

    WebElement pageHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(text(), 'Autentifica-te in contul tau')]")));
    assertTrue("Login page header is missing!", pageHeader.isDisplayed());

    WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("field-email")));
    emailField.sendKeys("testuser1740@mail.com"); 

    WebElement passwordField = driver.findElement(By.id("field-password"));
    passwordField.sendKeys("Test@1234");  

    WebElement loginButton = driver.findElement(By.id("submit-login"));
    loginButton.click();

    WebElement logoutButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("logout")));
    assertTrue("Logout button is not displayed!", logoutButton.isDisplayed());
}

@Test
public void addToFavoritesLoggedin() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    loginExistingUser();
    BaseFunctions.searchProductResults(driver);

    WebElement firstProductContainer = driver.findElement((By.cssSelector("#js-product-list .product-miniature")));
    WebElement wishlistButton = firstProductContainer.findElement(By.cssSelector(".wishlist-button-add"));
    wishlistButton.click();

    WebElement wishlistModal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".wishlist-modal.modal.fade.show")));
    assertTrue("Wishlist popup did not appear!", wishlistModal.isDisplayed());

    WebElement modalTitle = wishlistModal.findElement(By.cssSelector(".modal-title"));
    assertEquals("LISTELE MELE DE DORINTE", modalTitle.getText());

    WebElement firstWishlistItem = wishlistModal.findElement(By.xpath("(//ul[@class='wishlist-list']/li[@class='wishlist-list-item']/p)[1]"));
    String firstWishlistName = firstWishlistItem.getText().trim();
    assertEquals("Lista mea de dorinte", firstWishlistName);

    WebElement cancelButton = wishlistModal.findElement(By.className("close"));
    cancelButton.click();

    wait.until(ExpectedConditions.invisibilityOf(wishlistModal));
    wishlistButton.click();

    wishlistModal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".wishlist-modal.modal.fade.show")));

    WebElement addNewWishlistButton = wishlistModal.findElement(By.className("wishlist-add-to-new"));
    addNewWishlistButton.click();

    BaseFunctions.createNewWishlist(driver);

    WebElement addedWishlistItem = wishlistModal.findElement(By.xpath("(//ul[@class='wishlist-list']/li[@class='wishlist-list-item']/p)[2]"));
    String addedWishlistName = addedWishlistItem.getText().trim();
    assertEquals("Test Wishlist", addedWishlistName);

    addedWishlistItem.click();

    WebElement successAddMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".wishlist-toast.success")));
    assertTrue("Success message did not appear!", successAddMessage.isDisplayed());

    WebElement successMessageText = successAddMessage.findElement(By.cssSelector(".wishlist-toast-text"));
    assertEquals("Produs adaugat", successMessageText.getText().trim());
}

@Test
public void viewAddAndDeleteWishlist() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    loginExistingUser();

    WebElement myAccountLink = driver.findElement((By.cssSelector("a[href*='contul-meu']")));
    myAccountLink.click();
    
    assertTrue("Did not navigate to My Account page!", driver.getCurrentUrl().contains("contul-meu"));

    WebElement wishlistLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("wishlist-link")));
    assertTrue("Wishlist link did not appear!", wishlistLink.isDisplayed());

    wishlistLink.click();
    assertTrue("Did not navigate to Wishlist page!", driver.getCurrentUrl().contains("/module/blockwishlist/lists")); 
     
    WebElement wishlistContainer = driver.findElement((By.cssSelector(".wishlist-container")));
    assertTrue("Wishlist container did not appear!", wishlistContainer.isDisplayed());

    WebElement title = wishlistContainer.findElement(By.cssSelector(".wishlist-container-header h1"));
    assertEquals("Listele mele de dorinte", title.getText().trim());

    List<WebElement> wishlistItems = wishlistContainer.findElements(By.xpath("//ul[@class='wishlist-list']/li[contains(@class, 'wishlist-list-item')]"));

    WebElement firstWishlistElement = wishlistItems.get(0).findElement(By.cssSelector(".wishlist-list-item-title"));
    String firstWishlistName = firstWishlistElement.getText().trim();
    assertEquals("Lista mea de dorinte (0)", firstWishlistName);

    WebElement secondWishlistElement = wishlistItems.get(1).findElement(By.cssSelector(".wishlist-list-item-title"));
    String secondWishlistName = secondWishlistElement.getText().trim();
    assertEquals("Test Wishlist (1)", secondWishlistName);

    WebElement addNewWishlistButton = wishlistContainer.findElement(By.className("wishlist-add-to-new"));
    addNewWishlistButton.click();

    BaseFunctions.createNewWishlist(driver);

    wishlistItems = wishlistContainer.findElements(By.xpath("//ul[@class='wishlist-list']/li[contains(@class, 'wishlist-list-item')]"));
    WebElement addedWishlistElement = wishlistItems.get(2).findElement(By.cssSelector(".wishlist-list-item-title"));
    String addedWishlistName = addedWishlistElement.getText().trim();
    assertEquals("Test Wishlist (0)", addedWishlistName);

    BaseFunctions.deleteWhishlist(driver, 2);
    
}

@Test
public void addFirstProductToCart() {

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    String [] firstProduct = {"Melci Turbo brunneus", "Ai 1 produs in cos.", "25,83 LEI", "1", "25,83 LEI", "22,00 LEI", "7,63 LEI", "47,83 LEI (INCLUSIV TVA)"};

    BaseFunctions.searchProductResults(driver);

    WebElement firstProductContainer = driver.findElement(By.cssSelector("#js-product-list .product-miniature"));

    Actions actions = new Actions(driver);
    actions.moveToElement(firstProductContainer).perform();

    WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(firstProductContainer.findElement(By.cssSelector(".add-cart[data-button-action='add-to-cart']"))));
    addToCartButton.click();

    BaseFunctions.addToCart(driver, firstProduct);

    actions.sendKeys(Keys.HOME).perform();
    WebElement registerButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a [@title = 'Conecteaza-te la contul de client']")));

    loginExistingUser();

}

@Test
public void addProductMinQtyToCart() {

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    SoftAssert softAssert = new SoftAssert();

    String [] secondProduct = {"Melci Neritina Zebra", "Sunt 6 produse in cosul tau.", "12,95 LEI", "6", "77,70 LEI", "22,00 LEI", "15,92 LEI", "99,70 LEI (INCLUSIV TVA)"};

    BaseFunctions.searchProductResults(driver);

    List<WebElement> productContainers = driver.findElements(By.cssSelector("#js-product-list .product-miniature"));

    // Ensure there are at least 5 products
    if (productContainers.size() < 5) {
        throw new NoSuchElementException("Less than 5 products found on the page.");
    }

    WebElement fifthProductContainer = productContainers.get(4);

    Actions actions = new Actions(driver);
    actions.moveToElement(fifthProductContainer).perform();

    WebElement addToCartProductButton = wait.until(ExpectedConditions.elementToBeClickable(fifthProductContainer.findElement(By.cssSelector(".add-cart[data-button-action='add-to-cart']"))));
    addToCartProductButton.click();

    softAssert.assertTrue(BaseFunctions.checkCartModalIsOpened (driver), "Cart modal did not appear when trying to add a product with minimum quantity!");

    WebElement quickViewButton = fifthProductContainer.findElement(By.cssSelector(".quick-view"));
    quickViewButton.click();

    WebElement quickViewModal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".modal.fade.quickview.show")));
    assertTrue("Quick View modal did not appear!", quickViewModal.isDisplayed());

    WebElement productName = quickViewModal.findElement(By.cssSelector(".h1"));
    assertEquals(secondProduct[0], productName.getText().trim());

    WebElement productPrice = quickViewModal.findElement(By.cssSelector(".current-price .price"));
    assertEquals(secondProduct[2].toLowerCase(), productPrice.getText().trim());

    
    WebElement deliveryInfo = quickViewModal.findElement(By.cssSelector(".delivery-information"));
    assertEquals("Expediere imediata", deliveryInfo.getText().trim());
    
    WebElement quantityInput = quickViewModal.findElement(By.id("quantity_wanted"));
    assertEquals(secondProduct[3], quantityInput.getAttribute("value"));

    WebElement minQuantityAlert = quickViewModal.findElement(By.cssSelector(".product-minimal-quantity.alert.alert-info"));
    assertTrue("Minimum quantity alert not visible!", minQuantityAlert.isDisplayed());
    assertTrue("Minimum quantity message incorrect!", minQuantityAlert.getText().contains("Cantitatea minima de cumparat pentru acest produs este "+ secondProduct[3]));

    WebElement reviewStars = quickViewModal.findElement(By.cssSelector(".star_content a.scroll_review"));
    assertTrue("Review stars section missing!", reviewStars.isDisplayed());

    WebElement addToCartModalButton = quickViewModal.findElement(By.cssSelector(".btn-primary.add-to-cart"));
    addToCartModalButton.click();
    BaseFunctions.addToCart(driver, secondProduct);

    addToCartProductButton.click();
    softAssert.assertTrue(BaseFunctions.checkCartModalIsOpened (driver), "Cart modal did not appear when trying to readd a product that is already in cart!");

    // Assert all soft assertions
    softAssert.assertAll();

}

@Test
public void verifyCartPageAndCheckout() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    Actions actions = new Actions(driver);

    addFirstProductToCart();

    WebElement mainCartIcon = wait.until(ExpectedConditions.elementToBeClickable(By.className("font-shopping-cart")));
    actions.moveToElement(mainCartIcon).perform();

    WebElement finalizeOrderModalButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".cart-wishlist-action .cart-wishlist-checkout")));
    finalizeOrderModalButton.click();

    String expectedURL = "https://www.nevertebrate.ro/cos?action=show";
    wait.until(ExpectedConditions.urlToBe(expectedURL));
    assertEquals("Registration page did not load correctly!", expectedURL, driver.getCurrentUrl());

    WebElement cartTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cart-container .h1")));
    assertEquals("COSUL DE CUMPARATURI", cartTitle.getText().trim());

    WebElement firstCartItem = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cart-items .cart-item")));

    WebElement productTitle = firstCartItem.findElement(By.cssSelector(".product-line-info .label"));
    assertEquals("Melci Turbo brunneus", productTitle.getText().trim());

    WebElement productQuantity = firstCartItem.findElement(By.cssSelector(".js-cart-line-product-quantity"));
    assertEquals("1", productQuantity.getAttribute("value").trim());

    WebElement productPrice = firstCartItem.findElement(By.cssSelector(".product-price strong"));
    assertEquals("25,83 lei", productPrice.getText().trim());

    WebElement totalPrice = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cart-summary-line.cart-total .value")));
    assertEquals("47,83 lei", totalPrice.getText().trim());

    WebElement finalizeOrderCheckoutButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".checkout .btn-primary")));
    finalizeOrderCheckoutButton.click();

    wait.until(ExpectedConditions.urlContains("/checkout"));
    assertTrue("User was not redirected to the checkout page!", driver.getCurrentUrl().contains("checkout"));

}

@Test
public void verifyCheckoutPageAndFillMandatoryFields() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    Actions actions = new Actions(driver);
    
    verifyCartPageAndCheckout();

    WebElement checkoutTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("body#checkout")));
    assertTrue("Checkout page did not load!", checkoutTitle.isDisplayed());

    BaseFunctions.verifyCustomerBlockInCheckout(driver);
    BaseFunctions.fillAddressInformationInCheckout(driver);
    BaseFunctions.verifyDeliveryMethodsInCheckout(driver);
    BaseFunctions.verifyPaymentMethodsInCheckout(driver);
    BaseFunctions.verifyCartSectionInCheckout(driver);
    BaseFunctions.verifyMessageSectionInCheckout(driver);

    WebElement termsContainer = driver.findElement(By.id("conditions-to-approve"));
    actions.moveToElement(termsContainer).perform();
    
    WebElement termsCheckbox = termsContainer.findElement(By.id("conditions_to_approve"));
    assertTrue("Terms and conditions checkbox is not checked!", termsCheckbox.isSelected());


    WebElement paymentBlock = driver.findElement(By.cssSelector(".block-onepagecheckout.block-payment"));
    
    WebElement onlinePaymentOption= paymentBlock.findElement(By.cssSelector("label[for='payment-option-3']"));
    actions.moveToElement(onlinePaymentOption).click().perform();
    onlinePaymentOption.click();

    WebElement placeOrderButton = driver.findElement(By.cssSelector(".checkout.card-block button[name='submitCompleteMyOrder']"));
    actions.moveToElement(placeOrderButton).perform();
    placeOrderButton.click();

    WebElement errorAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".module_error.alert-danger")));
    assertTrue("Error alert is not displayed!", errorAlert.isDisplayed());
    
    String alertText = errorAlert.getText().trim();
    assertTrue("Alert message is incorrect!", alertText.contains("Nu a fost selectată nicio metodă de plată."));

    System.out.println("final test!");

}

@After
public void teardown() {
    driver.quit();
}

}