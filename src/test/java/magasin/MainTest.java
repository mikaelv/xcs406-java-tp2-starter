package magasin;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class MainTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final java.io.InputStream originalIn = System.in;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    private void provideInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    @Test
    public void testMenuComplet() {
        // Simulate user input: name, email, and immediate exit (option 6)
        String simulatedInput = "Test User\ntest@email.com\n6\n";
        provideInput(simulatedInput);

        // Run main
        Main.main(new String[]{});

        // Get the output
        String output = outContent.toString();

        // Verify menu content
        assertTrue(output.contains("--- Menu Magasin ---"), "Le menu doit avoir un titre");
        assertTrue(output.contains("1. Afficher les produits disponibles"), "Option 1 manquante");
        assertTrue(output.contains("2. Ajouter un produit au panier"), "Option 2 manquante");
        assertTrue(output.contains("3. Afficher le panier"), "Option 3 manquante");
        assertTrue(output.contains("4. Passer la commande"), "Option 4 manquante");
        assertTrue(output.contains("5. Afficher les commandes"), "Option 5 manquante");
        assertTrue(output.contains("6. Quitter"), "Option 6 manquante");
        assertTrue(output.contains("Au revoir !"), "Message de sortie manquant");
    }

    @Test
    public void testAffichageProduitsDisponibles() {
        // Simulate user input: name, email, display products (1), then exit (6)
        String simulatedInput = "Test User\ntest@email.com\n1\n6\n";
        provideInput(simulatedInput);

        // Run main
        Main.main(new String[]{});

        // Get the output
        String output = outContent.toString();

        // Split output into lines and count product entries
        String[] lines = output.split("\n");
        int productCount = 0;
        for (String line : lines) {
            if (line.contains("ID:") || line.contains("Prix:")) {
                productCount++;
            }
        }

        // We expect at least 2 products, and each product should have multiple attributes displayed
        assertTrue(productCount >= 2, "Au moins 2 produits doivent être affichés");
    }
} 