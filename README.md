ObjectiveSync
=============

A thin Java object persistence layer for JDBC.


**Downloading**

This library used to be on JCenter. Frankly, I cannot be bothered to jump through all the hoops to publish on Maven Central - I do this in my spare time, give me a break. So you can build it on your own and publish on your private repo - I enclose a recipe for PomFrites https://github.com/l3nz/pom_frites to make it easier. Enjoy.






News: 

* Feb 21, 2015: Version 0.1.4 will have a working Connection.

Ideas
-----

Great things in Hibernate:

* Association mapping: loading and saving associations in one easy step
* Database independence

Not-so-great things:

* What you get from Hibernate is not POJOs. Need for DAOs and copy, copy, copy. This is useless code and often breaks.
* Configuration too complex. You end up modelling everything around what works in Hibernate, not your classes (as promised) 
  or what works in the database (that is the real constraint you are facing)
* Hibernate-aware code everywhere 
* Different query language without a reasonable shell - you have to write code to test out a query
* Very complex and inefficient queries triggered for no apparent reason. You spend a day to control something
  that would have taken a few minutes in SQL
* Slow to start - big problem for testing.
* Bad practice - if you hide the database, you may get something done quickly, but it's a bad idea. 
  If yor Java code expects to have a collection of one million objects as an array, it does not matter 
  if they are lazily loaded or not - some code somewhere might want to iterate over them, and this will 
  kill the process. You cannot really forget that there is a database somewhere, and you should not do it.
* Aborts on commit. For long-lived transaction, you never know WHAT made the transaction abort. And what can you do next?

Aims
----

 * Minimal wrapper over JDBC.
 * Querying done in SQL. You should not be afraid of SQL. If you are, you should not be doing anything above the trivial CRUD.
 * Centralizing object marshaling and unmarshaling - each object should know how to sync itself and its descendents
 * Single syntax for inserting and updating
 * Ruby-like objectivized JDBC fetching with exception handling
 * User-definable deep fetching and updating (almost Hibernate-like).
 * Batch API to avoid round-trips when submitting multiple queries.
 * Stats collection and similar stuff.

Downloading
-----------

If you use Gradle (or any tool using Maven dependencies) you can simply declare the lib as:


	repositories {
		mavenCentral()
	    mavenRepo(url: 'http://jcenter.bintray.com') 
	}


	dependencies {
	    compile 'ch.loway.oss.ObjectiveSync:ObjectiveSync:0.1.4'
	}


This will download the package and all required dependencies (that is basically only the logging framework).


Getting started
---------------

You can see examples of simple operations under the 'tests/' folder. 

* Object are POJOs. No need for importing interfaces, annotations, etc. so they are easy to move to the client side using
  GWT or Jackson.
* For each object, you create a database accessor class, usually called "ObjectDB". This class extends ObjectiveFetch<T>.
  The ones we use for testing are in "ch.loway.oss.ObjectiveSync.maps".

The database accessor requires:

* a **table()** method that specifies the fields for your table
* a **load()** method that given a recordset row will build you an object and will schedule DeferredLoading for
  additional objects. So for example, in the Organization class you load all organizations and schedule
  deferred loading of all Persons who belong to them. This means you avoid the m*n problem when joining tables
  and that in the future we can schedule deferred loading to be efficient (e.g. batch, or in case you are loading 
  the same record multiple times)
* a **save()** method that will save your object as a row on the DB. If you do not need to save, don't implement it.
  Columns have valid defaults for insert and update.
* a **saveSubObjects()** that will save dependent objects.
* an **updatePrimaryKey()** that will be triggered on inserts.

How it all works

* PKs are strings (or numbers) and are created by the database layer. No composite keys supported.
* When loading objects, you can either create full queries (for aggregation queries, e.g. avg() count()  or whatever())
  or pass only the "WHERE ... GROUP BY ... ORDER BY ...." clauses - the object knows its own fields.
* Whatever goes wrong raises an SQLException
* The library is meant to be easy to use and to replace Hibernate in simple cases. When Java and the DB think
  different, the database wins.

We have a group for ObjectiveSync on G+ - https://plus.google.com/u/0/communities/108551537631226271215 - come and say hi.




