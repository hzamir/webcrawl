# webcrawler exercise

# 1. how to build it

Assuming for now we will write it as a spring-boot java microservice (which might be not a good choice)

mvn clean install

# 2. how to run it

java -jar webcrawler.jar

# 3. areas of expansion (and I haven't even started yet!)
* These could potentially run for a long time, so some intermittent updates would be good
* a visualization from the output, as a network diagram would be good
* provide authentication credentials to crawl parts of space that require authentication
* provide intelligible controls over the process, since there are many potential parameters
* list all urls with their actual download status, to distinguish urls that could not be traced from genuine dead ends
* make overall determination of success
    * are the results complete or truncated (e.g. due to a depth-limit)
    * were some of the links unfollowable
    * were there timeouts attempting to reach some of the resources
    * what about things that look like links that require actual evaluation to resolve (e.g. javascript generated links)
    