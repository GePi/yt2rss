-- changeset GePi:20240514-1
alter table public.channel
    drop constraint fk_channel_user_id;
alter table public.channel
    add constraint fk_channel_user_id
        foreign key (user_id) references public.users
            ON DELETE CASCADE;
alter table public.files
    drop constraint fk_files_channel_id;
alter table public.files
    add constraint fk_files_channel_id
        foreign key (channel_id) references public.channel
            ON DELETE CASCADE;
alter table public.user_roles
    drop constraint fk_user_id;
alter table public.user_roles
    drop constraint fk_role_id;
alter table public.user_roles
    add constraint fk_user_id
        foreign key (user_id) references public.users
            ON DELETE CASCADE;
alter table public.user_roles
    add constraint fk_role_id
        foreign key (role_id) references public.roles
            ON DELETE CASCADE;