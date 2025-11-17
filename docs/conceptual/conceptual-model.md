# Conceptual Model for E-Commerce Management System

 ## List of Classes
 
 1.User
 
 2.Product
 
 3.Category

 4.Order
 
 5.OrderItem
 
 6.Cart
 
 7.Payment
 
 8.Review
 
 9.Inventory
 
 10.Address

## Class Description

**1. User**

The User class represents any person interacting with the system, either as a customer or an administrator. It is essential because all activity—such as placing orders, writing reviews, or managing products—is tied to a user account. The class stores personal data like name, email, password, role, and account status.

**2. Product**

The Product class represents items that are available for purchase. It is one of the central components of the system, since users browse, add to cart, and buy products. It stores details such as product name, price, description, images, and references to its category and inventory.

**3. Category**

The Category class groups products under logical sections like Electronics, Clothing, or Home Appliances. It is important to help users filter and navigate products more easily. It stores category name, description, and maintains links to all associated products.

**4. Order**

The Order class represents a completed purchase made by a user. It is crucial for tracking transactions, delivery, and payment status. It stores information such as order date, total price, order status, and references to the user and order items inside it.

**5. OrderItem**

The OrderItem class represents each individual item inside an order. It helps calculate the total cost of an order and keeps track of product quantity purchased. It stores product reference, unit price, quantity, and subtotal.

**6. Cart**

The Cart class represents a user’s shopping cart before checkout. It temporarily stores products that the user intends to purchase. It keeps a list of items, quantities, subtotal price, and links to the related user.

**7. Payment**

The Payment class represents the payment transaction for an order. It is essential for validating and confirming purchases. It contains details such as payment method, payment status, transaction date, and links to the order.

**8. Review**

The Review class represents feedback from users about purchased products. It is important for improving product quality and helping other customers make informed decisions. It stores rating, comment, review date, user reference, and product reference.

**9. Inventory**

The Inventory class manages stock levels for each product. It ensures that items cannot be oversold and supports restocking operations. It stores current stock quantity, warehouse location, last updated date, and references the related product.

**10. Address**

The Address class represents shipping or billing addresses for users. It is required for successful order delivery. It stores street, city, postal code, country, and is associated with a specific user.

## UML Class Diagram

![Class Diagram](conceptual/ClassDiagram.jpg)


