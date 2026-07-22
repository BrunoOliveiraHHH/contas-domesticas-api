-- Validade da receita (recorrente): data_inicio e data_fim (nula = validade infinita).
-- Colunas em lancamento; para despesas ficam nulas.
alter table lancamento add column data_inicio date;
alter table lancamento add column data_fim date;

create index ix_lancamento_validade on lancamento (tipo, data_inicio, data_fim);
