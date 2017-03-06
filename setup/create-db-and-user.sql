CREATE DATABASE endorsement CHARACTER SET utf8 COLLATE utf8_general_ci;

CREATE USER 'endorsement_user'@'%' IDENTIFIED BY 'endorsement';
GRANT ALL PRIVILEGES ON endorsement.* TO 'endorsement_user'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;
