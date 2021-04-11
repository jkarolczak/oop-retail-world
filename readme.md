# Retail world
## Author
* Name: Jacek Karolczak

## About the project
The project was implemented as a coursework for the object oriented programming course. It is a very simple simulation of the world. Therefore it's strictly OP oreinted and things like design were not cared at all. There are many world features, which may be changed by a user. The main goal of the simulation is to simulate transmission of the disease over the population living in world, where only shops are opened. As the motivation for the project topic was probably coronavirus pandemic, I decided to add a small culture reference, to make you feel good (that's what she said).
<hr> 

## Running simulation 
1. To run program open CLI in directory containing `Retail.jar`.
2. Type: `java -jar Retail.jar` 
3. If necessary, change world parameters in section `Change world parameters` section. To apply changes press `Apply changes` button.
4. To start simulation press `Run simulation` button. This button is placed on left side of screen, in `Dashboard` section.
5. To start pandemy choose human and press `Make sick` button. This will be 'patient  0'. It is possible to make sick more than one human, but it has to be done one by one, no bulk operation available.
<hr>

## Information and control panel
* There are always up to date, automatically updating information available in `Dashboard` section. Available information:
    * Number of sick people
    * Number of people in mask
    * Number of vaccinated people
    * Number of all clients
    * Number of all suppliers
    * If there is a lockdown in the world
* There is possibility to change world parameters using `Change world parameters` section. Paramteres available to change:
    * Transmission rate base
    * Transmission rate for vaccinated people
    * Transmission rate for people with mask<br>
_Transmission rates are realtive values! For instance: person with mask will get sick with probability = [transmission rate base] * [transsmision rate for people with mask] etc. (applying independent events probability rule)._
    * Percentage of people with mask
    * Percentage of vaccinated people
    * Lockdown threshold
    * Number of shops which have to be visited before recovery

To apply changes press `Apply changes` button.
<hr>

## Shops
### Retail
* There are 10 retails on map.
* To display information about retail, press retail icon.
* Information available about each retail:
    * Name
    * Address
    * Storage capacity
    * Products in storage
    * Clients capacity
* There is no operation which may be done on retail shop.
### Wholesale
* There are 3 wholesales on map.
* To display information about wholesale, press wholesale icon.
* Information available about each retail:
    * Name
    * Address
    * Storage capacity
    * Products in storage
    * Processung capacity
* Operation which may be done on wholesale shop:
    * Adding new product, basing on `product name` and `product brand` given by user. To add product it is necessary to provide `product name`, `product brand` in respective input fields and confirm action using `Create product` button.
<hr>

## Humans
### Client
* Initialy there are 15 clients on the map.
* To display information about client, press client icon.
* Information available about each client:
    * Identifier
    * First name
    * Last name
    * Health status
    * Mask status
* Operation which may be done on client:
    * Making sick - in information window press `Make sick` button.
    * Deleting - in information window press `Delete` button.
* There is possibility to add new client to the world. This option is available on left side of screen, via `Create client` form.
### Supplier
* Initialy there are 5 suppliers on the map.
* To display information about supplier, press supplier icon.
* Information available about each supplier:
    * Identifier
    * Company name
    * Car brand
    * Number of products in trunk
    * Health status
    * Mask status
    * Route
* Operation which may be done on supplier:
    * Making sick - in information window press `Make sick` button.
    * Deleting - in information window press `Delete` button.
    * Changing route - in infromation window press `Change route`. The program will automatically generate new route and refresh the window.
* There is possibility to add new supplier to the world. This option is available on left side of screen, via `Create supplier` form.
