-- Seed do usuario administrador (admin/admin). Idempotente.
-- Hash BCrypt de "admin".
insert into usuario (login, nome_exibicao, senha, criado_em, criado_por)
values ('admin', 'Admin', '$2a$10$dN0FWUClzMIIIT7G0Mb7xOJUQ7uWVkc.MGdsH1PPCPkEaGBPOpPaS',
        now(), 'seed')
on conflict (login) do nothing;
