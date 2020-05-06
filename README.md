# Apache Ignite Integration with HashiCorp's Vault

By default, Apache Ignite requires that you put your passwords in plain-text
in your configuration file. Often that's against company policy and, even when
it isn't, it's just not a good idea.

Luckily it's not difficult to store your credentials elsewhere. It's [one line
of code to put them in environment
variables](https://gist.github.com/sdarlington/3975e867ed1b573a28189529027bd61a).
Using something like [Vault](https://www.vaultproject.io) it's terribly
difficult either, as this project shows. The code in this project absolutely
isn't production-ready, but it does work and should serve as a reasonable
starting point.

This repo comes with two classes:

* **VaultDataSourceProxy**. This class can be used when using Ignite's
third-party persiostence feature.
* **VaultSecurityCredentialsProvider**. This class can be used when using GridGain
Enterprise's enhanced security. These is no demonstration of this feature in this
repo currently.

## Setup

### Set up a MySQL database

Find a suitable user and schema to put a new, sample table. Then create it and
insert a little data:

```
create table Person (id integer primary key, name varchar(255), height integer);
insert into Person values (1,'Name1',180), (2,'Name2',160), (3,'Name3',170);
```

You'll need to update `config.xml` with your scheme/database names, JDBC URL
and usernames.

### Set up Vault

Run a development server locally:

```bash
vault server -dev
```

You'll need to get the "Root token" displayed in the output and put that in
the `config.xml` configuration file.
 
Then insert your credentials:

```bash
 vault kv put secret/mysql password=supersecretpassword
```

The key is configurable with the `passwordPath` property. The value should be
a single property, with the key being `password`.

With a real installation you wouldn't be using the root token or a development
server and you'd have authentication enabled...

### Build this project

The project is designed to be run from an IDE. Import the project into your
favourite one and run the build from there.

## Run

Run the Server class in the `test/java` directory. It should start up and load
the contents of your MySQL table into Ignite's memory and then logs the
contents to the screen. You can use `sqlline` to do more sophisticated queries
if you prefer.


 