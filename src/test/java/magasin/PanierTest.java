package magasin;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.ArrayList;

public class PanierTest {
    
    private Class<?> getClasse() {
        try {
            return Class.forName("magasin.Panier");
        } catch (ClassNotFoundException e) {
            fail("La classe Panier n'existe pas");
            return null;
        }
    }

    @Test
    public void testAttributsExistent() {
        Class<?> classe = getClasse();
        Field[] fields = classe.getDeclaredFields();
        
        // Vérifie que tous les attributs requis existent
        assertTrue(Arrays.stream(fields).anyMatch(f -> f.getName().equals("produits")), "L'attribut 'produits' est manquant");
        
        // Vérifie les types des attributs
        try {
            Field produitsField = classe.getDeclaredField("produits");
            assertEquals(ArrayList.class, produitsField.getType(), "L'attribut 'produits' doit être de type ArrayList");
            
            // Vérifie que c'est bien une ArrayList de Produit
            Type genericType = produitsField.getGenericType();
            assertTrue(genericType.toString().contains("Produit"), 
                "L'attribut 'produits' doit être une ArrayList<Produit>");
        } catch (NoSuchFieldException e) {
            fail("Un attribut requis est manquant");
        }
    }

    @Test
    public void testAttributsPrives() {
        Field[] fields = getClasse().getDeclaredFields();
        for (Field field : fields) {
            assertTrue(Modifier.isPrivate(field.getModifiers()), 
                "L'attribut '" + field.getName() + "' doit être privé");
        }
    }

    @Test
    public void testConstructeurExiste() {
        try {
            Constructor<?> constructeur = getClasse().getConstructor();
            assertTrue(Modifier.isPublic(constructeur.getModifiers()), "Le constructeur doit être public");
        } catch (NoSuchMethodException e) {
            fail("Le constructeur sans paramètres est manquant");
        }
    }

    @Test
    public void testMethodesExistent() {
        Class<?> classe = getClasse();
        Method[] methods = classe.getDeclaredMethods();
        
        // Vérifie l'existence des méthodes
        assertTrue(Arrays.stream(methods).anyMatch(m -> m.getName().equals("ajouterProduit")), 
            "La méthode ajouterProduit() est manquante");
        assertTrue(Arrays.stream(methods).anyMatch(m -> m.getName().equals("supprimerProduit")), 
            "La méthode supprimerProduit() est manquante");
        assertTrue(Arrays.stream(methods).anyMatch(m -> m.getName().equals("afficher")), 
            "La méthode afficherPanier() est manquante");
        assertTrue(Arrays.stream(methods).anyMatch(m -> m.getName().equals("calculerTotal")), 
            "La méthode calculerTotal() est manquante");
        
        try {
            // Vérifie les signatures des méthodes
            Method ajouterProduit = classe.getMethod("ajouterProduit", Class.forName("magasin.Produit"));
            Method supprimerProduit = classe.getMethod("supprimerProduit", Class.forName("magasin.Produit"));
            Method afficherPanier = classe.getMethod("afficher");
            Method calculerTotal = classe.getMethod("calculerTotal");
            
            assertTrue(Modifier.isPublic(ajouterProduit.getModifiers()), 
                "ajouterProduit() doit être public");
            assertTrue(Modifier.isPublic(supprimerProduit.getModifiers()), 
                "supprimerProduit() doit être public");
            assertTrue(Modifier.isPublic(afficherPanier.getModifiers()), 
                "afficherPanier() doit être public");
            assertTrue(Modifier.isPublic(calculerTotal.getModifiers()), 
                "calculerTotal() doit être public");
            
            assertEquals(void.class, ajouterProduit.getReturnType(), 
                "ajouterProduit() doit retourner void");
            assertEquals(void.class, supprimerProduit.getReturnType(), 
                "supprimerProduit() doit retourner void");
            assertEquals(void.class, afficherPanier.getReturnType(), 
                "afficherPanier() doit retourner void");
            assertEquals(double.class, calculerTotal.getReturnType(), 
                "calculerTotal() doit retourner double");
            
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            fail("Erreur lors de la vérification des signatures des méthodes: " + e.getMessage());
        }
    }

    @Test
    public void testAfficherPanierContenu() {
        try {
            Class<?> classe = getClasse();
            Constructor<?> constructeur = classe.getConstructor();
            Method afficherPanier = classe.getMethod("afficher");
            
            // Redirect System.out to capture printed output
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            System.setOut(new java.io.PrintStream(out));

            // Create a test panier using reflection
            Object panier = constructeur.newInstance();
            afficherPanier.invoke(panier);

            // Get the printed output
            String output = out.toString().trim();

            // Verify that some output is produced (even if empty)
            assertNotNull(output, "L'affichage ne doit pas être null");

            // Restore normal System.out
            System.setOut(System.out);
        } catch (Exception e) {
            fail("Exception lors du test d'affichage: " + e.getMessage());
        }
    }

    @Test
    public void testCalculerTotal() {
        try {
            // Get necessary classes and constructors
            Class<?> classePanier = getClasse();
            Class<?> classeProduit = Class.forName("magasin.Produit");
            Constructor<?> constructeurPanier = classePanier.getConstructor();
            Constructor<?> constructeurProduit = classeProduit.getConstructor(int.class, String.class, double.class);
            
            // Get necessary methods
            Method ajouterProduit = classePanier.getMethod("ajouterProduit", classeProduit);
            Method calculerTotal = classePanier.getMethod("calculerTotal");
            
            // Create test objects
            Object panier = constructeurPanier.newInstance();
            Object produit1 = constructeurProduit.newInstance(1, "Test1", 10.0);
            Object produit2 = constructeurProduit.newInstance(2, "Test2", 20.0);
            
            // Add products to cart
            ajouterProduit.invoke(panier, produit1);
            ajouterProduit.invoke(panier, produit2);
            
            // Calculate total
            double total = (double) calculerTotal.invoke(panier);
            
            // Verify total (10.0 + 20.0 = 30.0)
            assertEquals(30.0, total, 0.001, "Le total calculé devrait être 30.0");
            
        } catch (Exception e) {
            fail("Exception lors du test de calculerTotal: " + e.getMessage());
        }
    }
} 