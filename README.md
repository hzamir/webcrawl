# webcrawler exercise

# 1. how to build it

Assuming for now we will write it as a spring-boot java microservice (which might be not a good choice)

mvn clean package

# 2. how to run it (it requires Java 1.8)
From a terminal command line

cd to the target directory from the above build

java -jar webcrawler.jar

(If you put the application.yaml file in the same directory as the executable, you can edit the defaults that it uses
 Any uneditable values in the UI (currently specifying which types of links to follow)can be edited there prior to execution.
)

# 3. areas of expansion (and I haven't even started yet!)
* Can be packaged as an App on mac, exe on windows, etc.
* Offer headless option to run from command line without editing the parameters
* These could potentially run for a long time, so some intermittent updates status updates would be necessary
* From metrics a summary of overall completeness or success should be made
* Even better the link types that are followed could be checkable boxes in a gui control prior to running
* Possibly implement a breadth first search, since that would produce the most useful output when time limited or interrupted
* a visualization from the output, as a network diagram would be good
* provide authentication credentials to crawl parts of space that require authentication
* provide more intelligible/editable controls over the process, since there are many potential parameters
