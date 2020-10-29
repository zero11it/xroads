# Schema-free xroads description

Version 01

## General table properties
###### Tables below extend such general table, implementing the following fields
* **updated_at**: *timestamp* registering each variation of the record
* **source_id**: *varchar (512)* containing the id of the record as for the entity creator (aka Verticle generating the record)
* **external_references**: jsonb `{"(SOURCE)": {"id": (ID), "v": (VERSION), "e": (ERROR_MESSAGE), "u": (ERROR DATE), "s" : (STACK TRACE_ERROR)}}` containing the reference id of target/source datasource module along with the timestamp of the last update, the last error relating to the synchronization, if present, together with the stack trace and the date
* **data**: *jsonb* `{"(PROPERTY)":"(DATA)"}` custom properties managed by custom modules
* **version**: *int* representing software version that created the record. If json field internal structure changes it ensure that reading module is aware of such change
* **online**: *boolean* Allows to put the product online or not

## Product
###### Products are always considered composite products: they should have always at least one model
* **sku** *varchar(1024)* containing the absolute sku of the product. It must remain the same on all the platforms
* **brand** *varchar* containing the name of the manufacturer/brand
* **ean** *varchar* containing the ean code or the comma separated list of ean codes
* **name** *varchar* containing the absolute name of the product. It is not translated. Translation can be put in the tags
* **supplier** *varchar* containing the name of the supplier
* **virtual** *boolean* if the product is intangible and does not require shipment
* **cost** *numeric* the price payed for the product
* **weight** *real* the weight of the product. Its unit of measure must be user defined by the source module
* **descriptions** *jsonb* `{"(LANG_CODE)": {"(PLATFORM_ID)": (DESCRIPTION)} }` a key-value object containing translated descriptions
* **names** *jsonb* `{"(LANG_CODE)": {"(PLATFORM_ID)": (NAME)} }` a key-value object containing translated product names				
* **images** *jsonb* `{"urls": [(IMAGE_URL)], "binaries": [BINARY]}` a list of image locations

* **tags** *jsonb* `{ "(TAG_ID)": "(TAG_VALUE), ..., "translations": { "(TAG_ID)": {"(LANG_CODE)": {"(PLATFORM_ID)": (TRANSLATION)}}} }` list of tags associated to the product

__TAG_VALUE maybe a single *String* value or a *JsonArray*__

## Model
###### Represents variation of a product
* **product\_source_id** *varchar(1024)* it is used to apply model to a specific product. Product is identified by the source id
* **sku** *varchar(1024)* containing the absolute sku of the model. It must remain the same on all the platforms
* **ean** *varchar* containing the ean code or the comma separated list of ean codes
* **name** *varchar* containing the absolute name of the product. It is not translated. Translation can be put in the tags
* **availability_at** *timestamp* time when the model available stock has been updated
* **availability** *integer* current stock available for the model
* **tags** *jsonb* `{ "(TAG_ID)": "(TAG_VALUE), ..., "translations": { "(TAG_ID)": {"(LANG_CODE)": {"(PLATFORM_ID)": (TRANSLATION)}}} }` list of tags associated to the model
* **options** *jsonb* `{"(VARIANT_ID)":"(VALUE)", "translations": { "(VARIANT_ID)": {"(LANG_CODE)": {"(PLATFORM_ID)":"(TRANSLATION)"}} }` list of options determining model variation

## Price
###### Represents a price for a model given a combination of filters: no filter, country, user group, customer id
* **product\_source_id** *(varchar512)* it is used to apply price to a specific product. Product is identified by the source id
* **country** *varchar* price are applied when user's country match country. Use ISO country codes.
* **listing_group** *varchar* user group name is applied price
* **customer\_source_id** *varchar* it is used to apply price to specific customer. Customer is identified by the source id
* **discounted_price** *numeric* final price for user
* **retail_price** *numeric* price for send user
* **sell_price** *numeric* street official price
* **suggested_price** *numeric* street price suggested by supplier
* **buy_price** *numeric* cost of product
* **min_quantity** *numeric* minimum quantity for which this price list applies

## Stock
###### For each combination of model and warehouse there is always at most one stock representing current stock
* **availability** *integer* current available stock
* **model_source_id** *(varchar512)* it is used to apply stock to a model product. Model is identified by the source id
* **supplier** *varchar* name of supplier for this stock
* **warehouse** *varchar(512)* warehouse where stock is stored

## Customer
* **username** *varchar* username for user login
* **email** *varchar* customer's email
* **firstname** *varchar* first name, if person
* **lastname** *varchar* last name, if person
* **company** *varchar* company name
* **vat_number** *varchar* company vat number
* **fiscal_code** *varchar* person's fiscal code
* **date\_of_birth** *date* person's date of birth
* **language_code** *varchar* preferred language for website ui
* **phone** *jsonb* `{"home":"number", "mobile": "number"}` list of phones
* **addresses** *jsonb* `{"billing":{"name": "name", "address": "some address", ...}, {"shipping":{"name": "name", "address": "some address", ...}}` list of addresses
* **groups** *jsonb* `{"group1":true, "group2":false}` list of groups person is assigned. If parameter is true, group is automatically created and assigned to customer.
If parameter is false, the group is added to customer only if it is already existing (groups are created by external entities, which must associate group
to customer, if it is already existing). 
   
## Orders
###### Keeps track of orders collected by remote platforms
* **source** *(varchar)* the source name for this order
* **status** *(numeric)* the order status : 
    - 0 = ORDER_PENDING
    - 1 = ORDER_MONEYWAITING
    - 2 = ORDER_TODISPATCH
    - 3 = ORDER_DISPATCHED
    - 5 = ORDER_BOOKED
    - 6 = ORDER_DROPSHIPPING
    - 10 = ORDER_PENDING_NO_LOCK
    - 11 = ORDER_WISHLIST
    - 3001 = ORDER_WORKING_ON
    - 3002 = ORDER_READY
* **customer_source_id** *varchar* remote customer id of order
* **order_date**: *timestamp* 
* **payment_gateway** *varchar* 
* **currency** *varchar*
* **invoice_address** *jsonb* `{"vatNumber":"(NUMBER)", "addressee": "(ADDRESSEE)", "address": "(ADDRESS)", "zip"; "(ZIP)", "city": "(CITY)", "province": "(PROVINCE)", "country": "(COUNTRY)"}` address data		
* **shipping_address** *jsonb* `{"addressee": "(ADDRESSEE)", "address": "(ADDRESS)", "zip"; "(ZIP)", "city": "(CITY)", "region": "(REGION)", "country": "(COUNTRY)"}` address data
* **line_items** *jsonb* `{"(NUMBER OF ITEM)":{"sku":"(SKU)","tax":"(TAX)","vat":"(VAT)","name":"(NAME ITEM)","model_id":(MODEL_ID),"quantity":(QUANTITY),"unit_price":"(UNIT_PRICE)", "description":"(DESCRIPTION)","total_price":"(TOTALPRICE)","unit_listing":"(UNIT_LISTING)", "unit_taxable":"(UNIT_TAXABLE)", "total_taxable":"(TOTAL_TAXABLE)"}}` information about items in the order
* **totals** *jsonb*
* **dispatch_taxable** *numeric*
* **dispatch_vat** *numeric*
* **dispatch_total** *numeric*  
* **total** *numeric*
* **total_vat** *numeric*
* **customer_email** *(varchar)* customer email
* **anagrafica** *jsonb* `{"cel": "(CELL)", "pec": "(XXXXX)", "sdi": "(SDI)", "tel": "", "zip": "(ZIP)", "city": "(CITY)", "title": TITLE, "region": "(REGION)", "address": "(ADDRESS)"), "company": "(COMPANY)", "country": "(COUNTRY)", "lastName": "(LASTNAME)", "celPrefix": "(CELPREFIX)", "firstName": "(FIRSTNAME)", "telPrefix": "(TELPREFIX)", "vatNumber": "(VATNUMBER)", "validatedVatNumber": "(VALIDATORVATNUMBER)"}`

## Customdata
* **data_type** *varchar* label of custom data type
* **content** *jsonb* `{"property1":"data1", "property2": "data2"}` custom properties managed by this data type
