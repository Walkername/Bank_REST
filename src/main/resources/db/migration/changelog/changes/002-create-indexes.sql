create index idx_card_owner_id on card(owner_id);
create index idx_refresh_token_token on refresh_token(refresh_token);
create index idx_refresh_token_user on refresh_token(user_id);