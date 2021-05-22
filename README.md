# Your contribution is important!
If you found a mistake, you may fix it!

# Postgres
1. sudo apt update
2. sudo apt install postgresql postgresql-contrib
3. createuser --interactive
`Output
Enter name of role to add: crowdforce
Shall the new role be a superuser? (y/n) y`
4. createdb crowdforce


# How to run it?

1. You should create your telegram bot. Please send a message to @BotFather and create your bot.

0. Install docker.
1. Run mvn with cf-database install. (It should create Jooq models).
2. Set dev profile without telegram

https://core.telegram.org/api/obtaining_api_id


#mokc authorizaion

We use "Telegram Login Widget" for our security module.

If you want to use it locally you should doing simple steps:

https://core.telegram.org/widgets/login

apt-get install apache2

https://ngrok.com/

./ngrok http 80
