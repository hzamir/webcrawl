# webcrawler exercise

# 1. how to build it

Assuming for now we will write it as a spring-boot java microservice (which might be not a good choice)

mvn clean install

# 2. how to run it

java -jar webcrawler.jar


# 3. areas of expansion (and I haven't even started yet!)
* These could potentially run for a long time, so some intermittent updates status updates would be necessary
* It should be possible to stop the crawl at anytime
* Metrics should be included in json output format that specify number of different types of links, errors, statuses, etc.
* From metrics a summary of overall completeness or success should be made
* The followable link specifications should be externalized in yaml
* Even better the link types that are followed could be checkable boxes in a gui control prior to running
* I should possibly rewrite it as a breadth first search, since that would produce the most useful output when time limited or interrupted
* a visualization from the output, as a network diagram would be good
* provide authentication credentials to crawl parts of space that require authentication
* provide more intelligible/editable controls over the process, since there are many potential parameters
