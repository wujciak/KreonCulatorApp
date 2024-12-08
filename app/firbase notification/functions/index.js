/**
 * Import function triggers from their respective submodules:
 *
 /**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const {onDocumentCreated} = require("firebase-functions/v2/firestore");
const {logger} = require("firebase-functions");
const admin = require("firebase-admin");

// Inicjalizacja Firebase Admin SDK
admin.initializeApp();

// Funkcja powiadomień push dla nowego dokumentu w podkolekcji "messages"
exports.androidPushNotification = onDocumentCreated("chats/{forumId}/messages/{messageId}", async (event) => {
    try {
        // Pobranie danych nowej wiadomości
        const messageData = event.data.data(); // Dane nowo dodanej wiadomości
        const forumId = event.params.forumId; // Identyfikator forum

        // Przygotowanie wiadomości push
        const message = {
            notification: {
                title: "CreonCulator",
                body: `Nowa wiadomość na forum: ${forumId}`, // Dynamiczna treść powiadomienia
            },
            topic: "new_message", // Możesz zmienić na unikalny temat np. `forum_{forumId}`
        };

        // Wysyłanie wiadomości push
        const response = await admin.messaging().send(message);

        // Logowanie sukcesu
        logger.info("Powiadomienie wysłane pomyślnie:", response);
    } catch (error) {
        // Logowanie błędu
        logger.error("Błąd podczas wysyłania powiadomienia:", error);
    }
});

