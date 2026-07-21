-- Colunas de sincronizacao (uuid/versao/deletado) nas tabelas de dominio.
-- atualizado_em ja existe (auditoria) e serve de carimbo para o delta.

alter table usuario            add column uuid uuid, add column versao bigint not null default 0, add column deletado boolean not null default false;
alter table carteira           add column uuid uuid, add column versao bigint not null default 0, add column deletado boolean not null default false;
alter table categoria          add column uuid uuid, add column versao bigint not null default 0, add column deletado boolean not null default false;
alter table forma_pagamento    add column uuid uuid, add column versao bigint not null default 0, add column deletado boolean not null default false;
alter table mercado            add column uuid uuid, add column versao bigint not null default 0, add column deletado boolean not null default false;
alter table unidade_medida     add column uuid uuid, add column versao bigint not null default 0, add column deletado boolean not null default false;
alter table parametro          add column uuid uuid, add column versao bigint not null default 0, add column deletado boolean not null default false;
alter table preferencia        add column uuid uuid, add column versao bigint not null default 0, add column deletado boolean not null default false;
alter table lancamento         add column uuid uuid, add column versao bigint not null default 0, add column deletado boolean not null default false;
alter table recorrencia        add column uuid uuid, add column versao bigint not null default 0, add column deletado boolean not null default false;
alter table rateio             add column uuid uuid, add column versao bigint not null default 0, add column deletado boolean not null default false;
alter table participante_rateio add column uuid uuid, add column versao bigint not null default 0, add column deletado boolean not null default false;
alter table produto            add column uuid uuid, add column versao bigint not null default 0, add column deletado boolean not null default false;
alter table lista_compra       add column uuid uuid, add column versao bigint not null default 0, add column deletado boolean not null default false;
alter table item_compra        add column uuid uuid, add column versao bigint not null default 0, add column deletado boolean not null default false;
alter table cotacao_produto    add column uuid uuid, add column versao bigint not null default 0, add column deletado boolean not null default false;
alter table investimento       add column uuid uuid, add column versao bigint not null default 0, add column deletado boolean not null default false;
alter table aporte             add column uuid uuid, add column versao bigint not null default 0, add column deletado boolean not null default false;

create unique index uk_usuario_uuid on usuario (uuid);
create unique index uk_carteira_uuid on carteira (uuid);
create unique index uk_categoria_uuid on categoria (uuid);
create unique index uk_forma_pagamento_uuid on forma_pagamento (uuid);
create unique index uk_mercado_uuid on mercado (uuid);
create unique index uk_unidade_medida_uuid on unidade_medida (uuid);
create unique index uk_parametro_uuid on parametro (uuid);
create unique index uk_preferencia_uuid on preferencia (uuid);
create unique index uk_lancamento_uuid on lancamento (uuid);
create unique index uk_recorrencia_uuid on recorrencia (uuid);
create unique index uk_rateio_uuid on rateio (uuid);
create unique index uk_participante_rateio_uuid on participante_rateio (uuid);
create unique index uk_produto_uuid on produto (uuid);
create unique index uk_lista_compra_uuid on lista_compra (uuid);
create unique index uk_item_compra_uuid on item_compra (uuid);
create unique index uk_cotacao_produto_uuid on cotacao_produto (uuid);
create unique index uk_investimento_uuid on investimento (uuid);
create unique index uk_aporte_uuid on aporte (uuid);

create index ix_mercado_atualizado_em on mercado (atualizado_em);
create index ix_lancamento_atualizado_em on lancamento (atualizado_em);
