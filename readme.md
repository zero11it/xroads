# XRoads

XRoads is an ESB (Enterprise Service Bus) that provides two way integration between an ecommerce based on Rewix (https://www.rewixecommerce.com) and a generic ERP or WMS.

## Table of contents

- [Executive summary](#executive-summary)
- [Context](#context)
- [Glossary](#glossary)
- [Vision and goals](#vision-and-goals)
- [Principles](#principles)
- [Product](#product)
  - [How it works](#how-it-works)
- [Architecture](#architecture)
- [Installation and Usage](#installation-usage)
- [Privacy](#privacy)
- [Contributing](#contributing)
- [License](#license)
- [Open points](#open-points)

## Executive summary

- XRoads is a technological solution that provides integration and two way synch between an ecommerce based on Rewix Platform and almost any ERP or WMS.
- XRoads’s design and development are founded on five main principles: **utility, accuracy, scalability, transparency,** and **privacy.**
- XRoads primary duties are:
	- Route data between Rewix and ERP/WMS
	- Monitor and control routing of data exchange between services
	- Provide commodity services like data transformation and mapping, incremental processing or exception handling.
 
## Context

Integration between ERP/WMS Data and ecommerce website is very important in all ecommerce projects. Integration has to keep in synch information in terms of:

* products
* pricing
* stocks
* customers
* orders 

This document provides a description of XRoads. It is a good idea to read it first. 

Additionally, we are going to open-source XRoads’s software under the GNU Affero General Public License version 3. 

## Glossary

For ease of reference, the following list defines some of the terms used throughout this document.

**Analytics Service.** See [Architecture](#architecture).

**Backend Services.** See [Architecture](#architecture).

**Configuration Settings.** Settings.....

**Ecommerce** See [https://en.wikipedia.org/wiki/E-commerce](https://en.wikipedia.org/wiki/E-commerce)

**ERP** See [https://en.wikipedia.org/wiki/Enterprise_resource_planning](https://en.wikipedia.org/wiki/Enterprise_resource_planning)

**Orders** See [https://en.wikipedia.org/wiki/Sales_order#Electronic_sales_order](https://en.wikipedia.org/wiki/Sales_order#Electronic_sales_order)

**WMS** See [https://en.wikipedia.org/wiki/Warehouse_management_system](https://en.wikipedia.org/wiki/Warehouse_management_system)


## Vision and goals

XRoads is a technological solution that is designed to make easier integration between eshops and ecommerce..

It helps us to develop a two way synch:

1. Keep in synch catalog with products, prices and quantities
2. Keep in synch product information and images
3. Keep in synch orders and availabilities 

with ERP Master data repository.


## Principles

The main principles that guide the design and development of XRoads follow:

- **Utility.** The app needs to be useful in fulfilling the vision and goals for the project as outlined above. This is the most important principle.
- **Accuracy.** The tools aims to transmit and keep in synch only relevant data in the most accurate way.
- **Scalability.** XRoads needs to be widely adopted by all customers and users. This requires the system to scale well technologically to be manageable.
- **Transparency.** Everyone should be provided with access to documentation describing XRoads in all its parts and the rationale behind the most important design decisions. Also, all the relevant software will be open source. This allows the users to verify that the app works as documented and the expert community to help improve the system.
- **Privacy.** While keeping XRoads useful, privacy must be protected as well as possible. Earning and maintaining user trust is critical to make sure XRoads can be widely adopted.

## Product

XRoads is a technological solution that centres on an enterprise platform.

### How it works

Below, a high-level, simplified description of the system is provided. For more details, please study the rest of the XRads documentation.

Once installed and set up on a Server, XRoads is avalailable on port :8080

## Installation and usage

Coming soon

## Architecture

Coming soon

## Privacy

XRoads has been and continues to be designed and developed while paying a lot of attention to company data and user privacy. 

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License
[GNU Affero General Public License](https://www.gnu.org/licenses/agpl-3.0.en.html)
