ObjectiveSync
=============

A thin Java object persistence layer for JDBC.

Ideas
-----

Graet things in Hibernate:

* Association mapping: loading and saving associations in one easy step
* database independence

Not-so-great things:

* What you get from Hibernate is not POJOs. Need for DAOs and copy, copy, copy. This is useless code and often breaks.
* Configuration too complex. You end up modelling everything around what works in Hibernate, not your classes (as promised) 
  or what works in the database (that is the real constraint you are facing)
* Hibernate-aware code everywhere 
* Different query language without a reasonable shell - you have to write code to test out a shell
* Very complex and inefficient queries triggered for no apparent reason. You spend a day to control something
  that wpuld have taken a few minutes i SQL
* Slow to start 
* Bad practice - if you hide the database, you may get something done quickly, but it's a bad idea. 
  If yor Java code expects to have a collection of one million objcets as an array, it does not matter 
  if they are lazily loaded or not - some code somewhere might want to iterate over them, and this would 
  kill the process. You cannot really forget that there is a database somewhere, and you should not do it.
* aborts on commit. For long-lived transaction, you never know WHAT made the transaction abort.

Aims
----

 * centralizing object marshaling and unmarshaling - each object should know how to sync itself and its descendents
 * Ruby-like centralized JDBC fetching with exception handling
 * user-definable deep fetching and updating (almost Hibernate-like).
 * batch API to avoid round-trips
 * querying in SQL. You should not be afraid of SQL. If you are, you should not be doing anything above the trivial CRUD.
