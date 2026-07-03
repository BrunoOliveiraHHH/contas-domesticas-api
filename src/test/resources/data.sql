-- Seed do ambiente de TESTE (H2). Usuario admin/admin usado nos asserts.
-- Hash BCrypt de "admin" (hardcoded propositalmente para os testes).
insert into usuario (login, nome_exibicao, senha, criado_em, criado_por)
values ('admin', 'Admin', '$2a$10$dN0FWUClzMIIIT7G0Mb7xOJUQ7uWVkc.MGdsH1PPCPkEaGBPOpPaS',
        current_timestamp, 'seed');
