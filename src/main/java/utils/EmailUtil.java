package utils;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailUtil {

    //private static final String FROM_EMAIL = "hanae.arfa.2000@gmail.com";
    //private static final String APP_PASSWORD = "tfywwogzsykvxfvb";
    private static final String FROM_EMAIL = System.getenv("hanae.arfa.2000@gmail.com");
    private static final String APP_PASSWORD = System.getenv("tfywwogzsykvxfvb");

    public static boolean sendEmail(String to, String subject, String body) {
        try {
            Properties props = new Properties();

            // ✅ SMTP Gmail
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("✅ E-mail envoyé à : " + to);
            return true;

        } catch (Exception e) {
            System.out.println("❌ Erreur lors de l'envoi de l'e-mail : " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ✅ E-mail de confirmation d'inscription
    public static boolean sendInscriptionEmail(String to,
                                               String codeSession,
                                               String nom,
                                               String prenom,
                                               String dateExam,
                                               String heureDebut,
                                               String heureFin) {

        String subject = "Confirmation d'inscription - Code de session";
        String body =
                "Bonjour " + nom + " " + prenom + ",\n\n"
                + "Votre inscription est confirmée ✅\n"
                + "Code de session : " + codeSession + "\n\n"
                + "Créneau choisi : " + dateExam + " " + heureDebut + " - " + heureFin + "\n\n"
                + "Bon courage.";

        return sendEmail(to, subject, body);
    }

}
