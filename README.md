# Cafe y Vino Client App
The customer side of the Cafe y Vino system.
An Android app for customers of the restaurant.
## Summary
To use the app, customer must be registered with the Firebase Authentication. After the registration, the functionality depends on the state of customer. There are two possible states:
* 'not present' - customer is outside of the restaurant
* 'present' - customer is in the restaurant

### Functionality in the 'not present' state
* _Menu_ - customer can check out the menu, stored in the Cloud Firestore; for each product an image, a discription, and a price are available. The content of the displayed menu depends on the availability of products. The availability is controlled by the [admin side of the system](https://github.com/dimitriinc/cafe-y-vino-app-admin). All the images are stored in the Cloud Storage for Firebase.

* _Reservations_ - customer can make a reservation for any of the available days of the calendar. For greater flexibility, each day has two sets of reservations: one for the daytime hours and another for the nighttime hours. Thus, first customer chooses a date and a part of day, then they choose a table from the set of available (free of already made reservations) tables for that day and time, and then they choose the exact hour (according to the chosen part of day), and the amount of persons (according to the chosen table). The constructed reservation, with an optional comment from customer, is stored in the Cloud Firestore; a message is sent to the admin side via the Firebase Cloud Messaging and the [XMPP app server](https://github.com/dimitriinc/cafe-y-vino-app-server): admin can accept of reject the reservation.

### Functionality in the 'present' state
To enter the present state, during the working hours customer must send a request via FCM to the admin side: admin can accept and assign a table to the customer, or ignore the request.
* All the functionality from the 'not present' state is still available

* _Orders_ - customer can add any of the menu products to the shopping cart, represented by an SQLite table. Customer can manage their shopping cart by adding or removing products in it, and then send the constructed order to the admin side. The SQLite table is emptied, the order is stored in the Cloud Firestore, and an FCM message is sent to the admins.

* _Bill_ - the content of the orders, marked by admin as 'served', is moved to the bill of the customer, represented by a collection in the Cloud Firestore. Customer can track the state of the collection, with the appropriate prices for each product and the total price of the bill displayed on the screen. When ready, customer sends a request to pay the bill via the FCM, choosing one of the available payment methods.

* _Giftshop_ - a fidelity program. When the bill is marked as canceled by admin, 10% of its price is stored inside the customer's document in the Cloud Firestore as bonus points. Customer can spend the bonus points on various products, available as gifts.

## Messaging
The app employs a FirebaseMessagingService to receive downstream FCM messages sent from the admin side. On receiving a message, the app desplays the appropriate notification, based on the type of action, stored in the message's data payload.

## Technologies
* Java 1.8
* Room API 2.4.2
* Lifecycle API 2.4.1
* Firebase BOM 29.1.0
* Glide API 4.12.0

## Deployment
The app is [available](https://play.google.com/store/apps/details?id=com.cafeyvinowinebar.Cafe_y_Vino) on the Google Play Store.

## License
This project is licensed under the terms of the MIT license.
