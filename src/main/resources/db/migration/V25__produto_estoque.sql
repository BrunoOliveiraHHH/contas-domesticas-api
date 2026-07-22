-- Estoque por produto: minimo desejado e atual. A comprar = max(0, minimo - atual).
alter table produto add column estoque_minimo numeric(12, 3) not null default 0;
alter table produto add column estoque_atual  numeric(12, 3) not null default 0;
