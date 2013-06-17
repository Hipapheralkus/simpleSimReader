simpleSimReader
===============

This is a simple tool to see many useful information stored on SIM card. This however requires some smart-card reader plugged in USB port.

Anyone, who has no knowledge of PIN code can obtain information about: ATR (Answer To Reset), ICCID (Integrated Circuit Card Identification), MII (Major Industry Identifier), MCC (Mobile Country Code), MNC (Mobile Network Code), IIN (Issuer Identification Number), IAIN (Individual Account Identification Number), check digit, Servicce Provider Name, Phase, list of Preferred Languages, initialization status of PIN1/2 + PUK 1/2, number of tries left of PIN1/2 + PUK1/2.

Those, who also know PIN to SIM Card, can also read: IMSI (International Mobile Subscriber Identity), MCC (Mobile Country Code), Country, MNC (Mobile Network Code), Network operator, MSIN (Mobile Subscription Identification Number), LOCI (Location Information), TMSI (Temporary Mobile Subscriber Identity), LAI (Location Area Information), LOC (Location Area Code), TMSI TIME, Location Update Status, Last Numbers Dialed, Ciphering Key (K_c), GPRS Ciphering Key, Home Public Land Mobile Network Search Period, entire ADN (Abbreviated Dialing Numbers) = Telephone book (works with all kind of national characters, output is in UTF-16), all (even deleted!) SMS messages (also works with all kinds of national characters, output is in UTF-16). When viewing SMS, one can see if message is "free/deleted record", "message coming from the network and read", "message coming from the network and still to be read", "message sent to the network", "message to be sent to the network"; exact time in YYMMDDHHMMSS; whether message is single or multi part; telephone number of sender; and SMS message itself. List of all allocated and avaliable services is also shown.

All information can be saved into simple txt document.

Possible things for next development:
- multilanguage support
- export into XML and HTML
- changing of PIN1 and PIN2 codes
- unblocking of PIN1 or PIN2 codes with typing PUK1 or PUK2 codes
- allowing and disabling of PIN1 or PIN2 codes
- specialised terminal window to see communication on APDU level, with possibility of export
- see entire file system on SIM card with detailed information about all DF/EF (Dedicated File = folder, Elementary File = file) - type, access rights, size...
- editing SMS Messages, ADN entries (contacts in phone book)
- implement entire database of countries to MCC entries and operators to MNC entries
- unrecoverable erasement of SMS messages
- recovering of normal deleted SMS messages (changing their flag from deleted to received)
- possibility to choose concrete terminal from all terminals found on machine
