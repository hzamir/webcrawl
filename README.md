# Webcrawl Exercise

# 1. how to build it

Requirements
* Java 1.8
* Maven 3.3 or better (but tested with 3.5)

```bash
# from the webcrawl directory in a terminal
mvn clean package
```

# 2. how to run it

```bash
# from the webcrawl directory (it is directly executable on a mac
target/webcrawl-1.1.jar
```


(If you put the application.yaml file in the same directory as the executable, you can edit the defaults that it uses
 Any uneditable values in the UI (currently specifying which types of links to follow)can be edited there prior to execution.)

# 3. areas of expansion
* Provide more standardized output options
* Can be packaged as an App on mac, exe on windows, etc.
* Offer headless option to run from command line without editing the parameters
* These could potentially run for a long time, so some intermittent updates status updates would be necessary
* From metrics a summary of overall completeness or success should be made
* Even better the link types that are followed could be checkable boxes in a gui control prior to running
* a visualization from the output, as a network diagram would be good
* provide authentication credentials to crawl parts of space that require authentication
* provide more intelligible/editable controls over the process, since there are many potential parameters
* Provide at minimum tooltips to clarify operation of the application
* Provide a **non-redundant format** that references existing nodes
 


# 4. some thoughts on the process
* My first thought was just to provide a one line wget based implementation, however
  * I perceived that was not the spirit of the exercise
  * It would require installing wget on a mac
  * However I did try it right away as a reference, and to learn something about webcrawlers:
  
```bash

#!/bin/bash

DOMAINS=$1
HOME="http://${DOMAINS}"

#GREP='\.\(css\|js\|png\|gif\|jpg\)$'
GREP='\.\(css\|js\|png\|gif\|jpg\)$'
DEPTH=5

# lets pull some promising looking wget options out the help command, to see what might work for crawlers
# -b or --background run in background
# -r or --recursive
# -l or --level limit on how deeply it is permitted to recurse  (comparing say -l=9 to -l=10 to see if they produce the same result would tell us if there is more without going unlimited)
# --spider don't download anything
# --force-html treat input file as html (how would this interact with not filtering out other types of links?)
# -D or --domains=<list> comma separated list of domains that are acceptable to crawl through
# These options might help with not tripping defensive response from the website
# --limit-rate=<rate> restrict download rate (what units?) 
# -w --wait=<seconds>  how long to wait between retrievals (sites don't necessarily like you crawling esp. at high speed)
# --random-wait will wait from 0.5 .. 1.5 times the wait interval to randomize waits to make it more organic behavior
 
wget -r --spider --delete-after --force-html -D "$DOMAINS" -l $DEPTH "$HOME" 2>&1  | (some postprocessing here)

```
* Examining the wget parameters was instructive, in particular:
  * Webcrawling aggressively could be treated like a denial of service attack, this is why wget provides throttling and randomization  
* My next thought was that this could run for a totally unbounded period of time, and some database persistence might be needed.
  * I did not elect to implement this, since I felt it is primarily an exercise, but the map I used to determine if a url had previously been visited, and the node structures could be moved to value=key database
* Looked for a suitable library to help parse for links and found JSoup, since this was a java library, I decided to go with Spring boot as a pattern for putting together an application
* Initially was thinking of making this into a kind of a webservice, where I could kick off a webcrawl, and then query later at any time for interesting information with rest calls
* Decided to keep it simple, especially since there were so many parameters, I decided to give it a GUI interface
* Put the initial values in application.yaml file, and allow the user to override them
* Started providing a Swing GUI, since it was convenient, but it was awful, so I decided to learn JavaFX instead
* Found a library that someone had used to more elegantly combine Spring boot with Java FX applications so I incorporated that
* Implemented as depth-first recursion. Any recursion could be rewritten as iteration to prevent a stack explosion
  * The depth limit should mitigate the need for that, especially if configured so the upper limit is useful and not a problem
  * Even recursive depth exceeded stack capacity, it should gracefully recover (though multiple such exceptions would be very ineffecient compared to reducing the depth limit)
* I saw no need to choose a specific serialization format, I used jackson library (commonly used in spring rest calls) to support multiple output formats
  * xml, json, yaml
  
  

