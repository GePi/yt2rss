-- changeset GePi:100500-1
ALTER TABLE "channel"
    ADD "version" INTEGER NOT NULL default 0;
ALTER TABLE "user_roles"
    ADD "version" INTEGER NOT NULL default 0;
ALTER TABLE "roles"
    ADD "version" INTEGER NOT NULL default 0;
ALTER TABLE "users"
    ADD "version" INTEGER NOT NULL default 0;
ALTER TABLE "files"
    ADD "version" INTEGER NOT NULL default 0;


