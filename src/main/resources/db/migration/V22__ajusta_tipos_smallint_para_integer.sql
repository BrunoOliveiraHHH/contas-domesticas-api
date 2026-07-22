-- Alinha as colunas de dias/parcelas ao tipo esperado pelas entidades JPA.
-- As entidades mapeiam esses campos como Integer (integer/int4), mas as migracoes
-- originais (V6, V12, V13) as criaram como smallint (int2), o que faz o
-- hibernate.ddl-auto=validate falhar no PostgreSQL.
-- smallint -> integer e um cast implicito seguro (sem perda de dados).
alter table forma_pagamento alter column dia_fechamento type integer;
alter table forma_pagamento alter column dia_vencimento type integer;
alter table recorrencia      alter column dia_vencimento type integer;
alter table lancamento       alter column numero_parcela type integer;
alter table lancamento       alter column total_parcelas type integer;
