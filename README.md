Tombstoning note:

This repository have been archived and exists for historical purposes. 
No updates or futher development will go into this repository. The content can be used as is but no support will be given. 

---


Important:
==========

PDFChecker (and possibly others) need JHove installed.

As of 2015-09-03 JHove 1.12 is heavily under development after
the transition to OpenPreserve.  TRA has chosen to develop
against the "integration" branch (which is the stable branch
as opposed to master which is the development branch).  For the
pilot we gave up on using jhove as a library, and chose to use
it as a command line utility instead.  For production this
will be too slow.

Instructions to install JHove as a command line utility under
$HOME/jhove-beta (which is where the code explicitly looks for
jhove now):

    git clone git@github.com:openpreserve/jhove.git
    cd jhove
    git checkout c79f36425c155e4a42c33ef13141c02aaa4e9fe1
    mvn clean package
    java -jar jhove-installer/target/jhove-xplt-installer-*.jar

(and answer the izpack installer questions).

The commit used was the newest on the integration branch at the
time of writing.





