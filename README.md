# PharmacyPOS
Software Requirements Specification

Pharmacy POS System

Introduction
1-	Purpose
This document defines the requirements for a Pharmacy Point of Sale (POS) and Management System designed for pharmacies operating in Sudan.
The system will support:
•	Retail drug sales (OTC + prescription) 
•	Inventory with batch & expiry tracking 
•	Supplier and purchasing workflows 
•	Financial reporting in SDG (Sudanese Pound)
2-	Scope
The system will:
•	Handle daily pharmacy operations 
•	Ensure drug safety (expiry + batch tracking) 
•	Support fast checkout 
•	Provide basic accounting visibility 
The system will NOT:
•	Replace full ERP/accounting systems 
•	Integrate with insurance (optional future scope)
•	Consider promotions and discounts (allowed to be handled manual)
3-	Definitions
•	OTC: Over-the-counter drugs (no prescription required) 
•	Prescription drugs: Must be dispensed with a doctor prescription 
•	Batch Number: Manufacturer identifier for drug production 
•	Expiry Date: Validity of the drug 
•	Near Expiry: Typically, within 3–6 months 
 Users & Roles
2.1 Roles
Role	Description
Pharmacist	Dispensing drugs, verifying prescriptions
Cashier	Sales and billing
Store Keeper	Inventory & stock receiving
Owner/Manager	Reports, pricing, monitoring
________________________________________
2.2 Permissions (Simplified)
•	Pharmacist: 
o	Can sell all items 
o	Can override warnings (expiry, prescription) 
•	Cashier: 
o	Can sell OTC only (optional restriction) 
•	Store Keeper: 
o	Can add stock, batches, expiry 
•	Owner: 
o	Full access
Note: All the roles can be given to the Owner or the Pharmacist.
In the first release of the software the client will be given a limited number of user accounts all have equal permissions .

System Overview
The system consists of modules:
1.	Sales (POS) 
2.	Inventory Management 
3.	Purchasing 
4.	Expiry & Batch Tracking 
5.	Reporting 
6.	User Management
Functional Requirements
1-	Sales Module (Core POS)
Workflow:
1.	User opens POS screen – a consumer orders an item
2.	Search item by: 
o	Name  
o	Barcode by using the barcode scanner
3.	Select item 
System automatically: 
o	The system only shows the earliest expiry batch (FEFO) – (The store keeper stock the items in the shelfs according to the batch number). Then the system shows the next batch based on the ordered quantity.
o	In case of prescription required drug: a form pops up to be filled with the prescription data which must be saved to be browsed by the user from the reporting module later.
4.	Enter quantity 
5.	System checks: 
o	Stock availability according to batch. In case the selected batch not available the POS will show the next in the FEFO and let the cashier to sell from it the remaining.
For example:
If the consumer ordered 11 units from ‘Panadol’ and the FEFO found batch B-123 has only 9. The POS will select all the 9 units and show two from the next batch in the FEFO.
6.	The user reviews the ordered items list with the consumer.
7.	Select payment method: 
o	Cash
o	Bank
o	Part cash and part bank
8.	Print receipt
Key Rules:
•	Cannot sell expired items – The POS will not select an expired item.
•	Show warning for near-expiry items 
•	Use FEFO (First Expiry First Out) logic 
2-	Inventory Management Module
Features:
•	Add new product: 
o	Name 
o	barcode
o	Category 
o	supplier
o	Selling price
o	Purchasing price
o	Stock
•	Track stock by: 
o	Batch number 
o	Expiry date 
o	Quantity
•	Allow price changes
•	Purchase
•	Search for a particular product by name/ barcode
•	Edit a product detail

Product	Batch	Expiry	Qty
Panadol	B123	12/2026	50
Panadol	B456	05/2025	20

3-	Purchasing Module
This is a process of completing a purchase for a particular item when a supplier is delivering this item to the pharmacy. It can be implemented as a button in the inventory management module screen that once pressed a form pops up and allow the user to complete the following workflow for one item.
Workflow:
1.	Add supplier or Select supplier (if exists) 
2.	Add item
3.	For the item: 
o	Batch number 
o	Expiry date 
o	Purchase price
o	Selling price
o	Quantity
4.	Confirm purchase 
5.	Update the Stock in inventory (This done automatically by the system while the user can review and edit)
Key Requirements:
•	Track supplier history 


4-	Batch & Expiry Tracking
This module is used by the system automatically as a background process in the beginning of each session and generates reports – which can be browsed later by the user in the Reporting module-  and automatically pop-up alerts – in the POS screen- as a result of it 
System must:
•	Track: 
o	Batch number 
o	Expiry per batch 
•	Auto-alert: 
o	Near expiry (configurable: 90 days) 
•	Generate reports: 
o	Expired items 
o	Near-expiry items
Alerts:
•	On POS → warning popup 
•	Dashboard → summary
5-	Reporting Module
Required Reports:
•	Daily sales report 
•	monthly sales report 
•	Profit report by month/ three months/ six months/ one year
•	Stock report 
•	Expiry report 
•	Fast-moving items
•	Prescription drugs details
Important to note:
• Handle currency fluctuation 
• Show profit per batch

6-	User Management
•	Login system  
•	Activity logs (who sold what)
•	Add/Edit user account and change password
Non-Functional Requirements
Performance
•	POS must respond in < 2 seconds 
•	Search must be fast even with large inventory
Usability
•	Simple UI (many users not highly technical) 
•	Arabic + English support
Reliability
•	Must work offline
•	Local database
Security
•	Login required 
•	Basic audit logs
•	Unauthorized Penetration block 

Business Rules
Rule 1: FEFO (First Expiry First Out)
•	Always sell earliest expiry first
Rule 3: Price Instability
•	Prices change frequently 
•	System must allow: 
o	Easy price updates 
o	Historical profit tracking
Rule 4: Prescription Control
•	Some drugs must require prescription form to be filled.
Key Screens (UI Requirements)
1.	POS Screen
2.	Inventory Management
3.	Reports Dashboard
4.	Users

Future Enhancements
•Mobile app 
•Cloud sync
•Multi-branch support
