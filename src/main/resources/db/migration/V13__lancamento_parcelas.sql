alter table lancamento add column grupo_parcela uuid;
alter table lancamento add column numero_parcela smallint;
alter table lancamento add column total_parcelas smallint;

create index ix_lancamento_grupo_parcela on lancamento (grupo_parcela);
