commcare-api module
===================

About
-----

commcare-api is used to access data from CommCare, using the export APIs of CommCareHQ described [here](https://confluence.dimagi.com/display/commcarepublic/Export+API).

What does it provide?
---------------------

It provides a way to fetch form instances from CommCareHQ, for a specified set of form types (or form definitions).
Also, it keeps track of previously fetched forms so that it does not fetch it again. The entry point to this service is:
**CommCareFormImportService#fetchForms()**

What do I need to do to make this work?
---------------------------------------

* couchdb.properties: This module uses the motech-platform-common module, to access CouchDB, so that it can store the
  previous export tokens. So, it expects a file called "couchdb.properties" to be present in the classpath. It needs to
  provide information about the CouchDB instance. Here's an [example file](../src/test/resources/couchdb.properties).

* commcare-import.properties: This file is also expected to be in the classpath. At this point, it has only one
  property, which is the path to the JSON form definition file (described below). Here's how it might look (contents of
  the file):

        commcare-export.definition.file=/path/in/classpath/to/commcare-export.json

* commcare-export.json: The JSON form definition file. This is expected to be somewhere in the classpath. It describes
  the forms to be downloaded from CommCareHQ. This JSON is converted into an object of the CommCareFormDefinitions
  class. So, the fields in that class need to correspond to the JSON. Here's a sample JSON file:

        {
            "userName": "someUser@gmail.com",
            "password": "somePassword",
            "forms" : [
                {
                    "name": "Registration",
                    "url": {
                        "base": "https://www.commcarehq.org/a/abhilasha/reports/export/",
                        "queryParams": {
                            "nameSpace": "http://openrosa.org/formdesigner/UUID-OF-FIRST-FORM"
                        }
                    },
                    "mappings": {
                        "form|path|to|field" : "FieldInOutput",
                        "form|path|to|another|field" : "AnotherFieldInOutput"
                    }
                },
                {
                    "name": "SomeOtherForm",
                    "url": {
                        "base": "https://www.commcarehq.org/a/abhilasha/reports/export/",
                        "queryParams": {
                            "nameSpace": "http://openrosa.org/formdesigner/UUID-OF-SECOND-FORM"
                        }
                    },
                    "mappings": {
                        "form|path|to|field" : "FieldInOutput",
                        "form|path|to|another|field" : "AnotherFieldInOutput"
                    }
                }
            ]
        }

  The *userName* and *password* fields apply to all the forms. This has to be a valid "Web user" in CommCareHQ. The forms
  have *name* and *url* fields. The *queryParams -> nameSpace* field has the namespace of the form, which can be seen in
  CommCareHQ.

  The *mappings* specify which fields of the form we care about. The left-hand-side of the mappings can be got by
  looking at the CSV/JSON exports from CommCareHQ. Normally, they'll look like "form|Mother|Name" or
  "form|Mother_Name". The right-hand-side of the mappings specify the key of the resulting fields.
  
Description of the process
--------------------------

1. Assume that the form definition sample provided above is the real form definition.

2. When **CommCareFormImportService#fetchForms()** is called, it hits CommCareHQ with a URL similar to the one provided
   below, with the specified username and password:

        https://www.commcarehq.org/a/abhilasha/reports/export/?export_tag=%22http://openrosa.org/formdesigner/UUID-OF-FIRST-FORM%22&format=json

3. Assume that the JSON which is returned looks like this:

        {
          "#" : {
            "headers" : [
              "form|path|to|field",
              "form|path|to|another|field",
              "form|path|to|field|which|is|not|mapped"
            ],
            "rows" : [
              [
                "Value of Field 1 in form 1",
                "Value of Field 2 in form 1",
                "Value of Field 3 in form 1, not mapped"
              ],
              [
                "Value of Field 1 in form 2",
                "Value of Field 2 in form 2",
                "Value of Field 3 in form 2, not mapped"
              ]
            ]
        }

4. So, the JSON has two forms in it (in the *rows* list). The **CommCareFormImportService#fetchForms()** call converts the
   JSON into two instances of the CommcareFormInstance class. Using the instance (say it is called formInstance), and
   doing this:

          formInstance.contents()

   you'll get a map which has this data:

          "FieldInOutput" => "Value of Field 1 in form 1",
          "AnotherFieldInOutput" => "Value of Field 2 in form 1"

   Notice that it does not have the third (unmapped) field.

