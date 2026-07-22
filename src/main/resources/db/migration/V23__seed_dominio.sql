-- Seed de dados de DOMINIO / referencia (formas de pagamento, unidades de medida,
-- indices economicos, aliquotas de IR e preferencias globais como moeda e formato de data).
-- Idempotente (guardas WHERE NOT EXISTS / ON CONFLICT) para conviver com seeds manuais.

-- ===== Formas de pagamento (dominio) =====
insert into forma_pagamento (nome, tipo, ativa, dia_fechamento, dia_vencimento, criado_em, criado_por)
select 'Dinheiro', 'DINHEIRO', true, null, null, now(), 'flyway'
where not exists (select 1 from forma_pagamento where nome = 'Dinheiro');
insert into forma_pagamento (nome, tipo, ativa, dia_fechamento, dia_vencimento, criado_em, criado_por)
select 'PIX', 'PIX', true, null, null, now(), 'flyway'
where not exists (select 1 from forma_pagamento where nome = 'PIX');
insert into forma_pagamento (nome, tipo, ativa, dia_fechamento, dia_vencimento, criado_em, criado_por)
select 'Debito', 'DEBITO', true, null, null, now(), 'flyway'
where not exists (select 1 from forma_pagamento where nome = 'Debito');
insert into forma_pagamento (nome, tipo, ativa, dia_fechamento, dia_vencimento, criado_em, criado_por)
select 'Cartao Itau', 'CREDITO', true, 20, 10, now(), 'flyway'
where not exists (select 1 from forma_pagamento where nome = 'Cartao Itau');
insert into forma_pagamento (nome, tipo, ativa, dia_fechamento, dia_vencimento, criado_em, criado_por)
select 'Cartao Nubank', 'CREDITO', true, 3, 10, now(), 'flyway'
where not exists (select 1 from forma_pagamento where nome = 'Cartao Nubank');
insert into forma_pagamento (nome, tipo, ativa, dia_fechamento, dia_vencimento, criado_em, criado_por)
select 'Boleto', 'BOLETO', true, null, null, now(), 'flyway'
where not exists (select 1 from forma_pagamento where nome = 'Boleto');
insert into forma_pagamento (nome, tipo, ativa, dia_fechamento, dia_vencimento, criado_em, criado_por)
select 'Transferencia', 'TRANSFERENCIA', true, null, null, now(), 'flyway'
where not exists (select 1 from forma_pagamento where nome = 'Transferencia');

-- ===== Unidades de medida (dominio) =====
insert into unidade_medida (nome, sigla, tipo, fator_para_base, criado_em, criado_por) values
  ('Unidade', 'un', 'UNIDADE', 1, now(), 'flyway'),
  ('Caixa', 'cx', 'UNIDADE', 1, now(), 'flyway'),
  ('Pacote', 'pct', 'UNIDADE', 1, now(), 'flyway'),
  ('Quilograma', 'kg', 'PESO', 1, now(), 'flyway'),
  ('Grama', 'g', 'PESO', 0.001, now(), 'flyway'),
  ('Litro', 'l', 'VOLUME', 1, now(), 'flyway'),
  ('Mililitro', 'ml', 'VOLUME', 0.001, now(), 'flyway'),
  ('Metro', 'm', 'COMPRIMENTO', 1, now(), 'flyway')
on conflict (sigla) do nothing;

-- ===== Parametros: indices economicos, aliquotas de IR e limites (dominio) =====
insert into parametro (chave, valor, vigencia_inicio, descricao, criado_em, criado_por)
select v.chave, v.valor, date '2026-01-01', v.descricao, now(), 'flyway'
from (values
  ('SELIC', 0.1500, 'Taxa Selic anual (fracao)'),
  ('CDI', 0.1490, 'CDI anual (fracao)'),
  ('IPCA', 0.0450, 'IPCA anual estimado (fracao)'),
  ('IR_ATE_180', 0.2250, 'IR regressivo ate 180 dias'),
  ('IR_181_360', 0.2000, 'IR regressivo 181-360 dias'),
  ('IR_361_720', 0.1750, 'IR regressivo 361-720 dias'),
  ('IR_ACIMA_720', 0.1500, 'IR regressivo acima de 720 dias'),
  ('LIMITE_COMPROMETIMENTO', 0.3000, 'Limite de comprometimento da renda (fracao)'),
  ('DIAS_ALERTA_VENCIMENTO', 5.0000, 'Dias de antecedencia para alerta de vencimento')
) as v(chave, valor, descricao)
where not exists (
  select 1 from parametro p where p.chave = v.chave and p.vigencia_inicio = date '2026-01-01'
);

-- ===== Preferencias globais (usuario_id NULL): moeda, locale, formato de data, tema =====
insert into preferencia (chave, valor, usuario_id, criado_em, criado_por)
select v.chave, v.valor, null, now(), 'flyway'
from (values
  ('MOEDA_PADRAO', 'BRL'),
  ('SIMBOLO_MOEDA', 'R$'),
  ('LOCALE', 'pt-BR'),
  ('FORMATO_DATA', 'DD/MM/AAAA'),
  ('TEMA', 'light')
) as v(chave, valor)
where not exists (
  select 1 from preferencia p where p.chave = v.chave and p.usuario_id is null
);
