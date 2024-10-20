-- Create the script tables
CREATE TABLE bse_eq_scrip (symbol TEXT UNIQUE);
CREATE TABLE ncdex_scrip (symbol TEXT UNIQUE);
CREATE TABLE bse_fo_scrip (symbol TEXT UNIQUE);
CREATE TABLE nse_corp_scrip (symbol TEXT UNIQUE);
CREATE TABLE ncdex_fo_scrip (symbol TEXT UNIQUE);
CREATE TABLE nse_com_scrip (symbol TEXT UNIQUE);
CREATE TABLE nse_fo_scrip (symbol TEXT UNIQUE);
CREATE TABLE nse_curr_scrip (symbol TEXT UNIQUE);
CREATE TABLE nse_curr_opt_scrip (symbol TEXT UNIQUE);
CREATE TABLE nse_eq_scrip (symbol TEXT UNIQUE);
CREATE TABLE nse_idx_scrip (symbol TEXT UNIQUE);
CREATE TABLE amfi_scrip (symbol TEXT UNIQUE);




-- Create or replace the trigger function
CREATE OR REPLACE FUNCTION bse_eq_scrip_function()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO bse_eq_scrip (symbol)
    VALUES (TRIM(NEW.sc_name));
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create or replace the trigger function
CREATE OR REPLACE FUNCTION ncdex_scrip_function()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO ncdex_scrip (symbol)
    VALUES (TRIM(NEW.fininstrm_nm));
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION bse_fo_scrip_function()
RETURNS TRIGGER AS $$
BEGIN
    -- Concatenate the specified columns, remove spaces, and insert into bse_fo_scrip
    INSERT INTO bse_fo_scrip (symbol)
    VALUES (TRIM(REPLACE(NEW.symbol || NEW.expiry || NEW.strike || NEW.option_type, ' ', '')));
    
    RETURN NULL; -- Return NULL as we don't need to return anything for triggers
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION nse_corp_scrip_function()
RETURNS TRIGGER AS $$
BEGIN
    -- Concatenate the specified columns, remove spaces, and insert into nse_corp_scrip
    INSERT INTO nse_corp_scrip (symbol)
    VALUES (TRIM(REPLACE(NEW.symbol || NEW.security, ' ', '')));
    
    RETURN NULL; -- Return NULL as we don't need to return anything for triggers
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ncdex_fo_scrip_function()
RETURNS TRIGGER AS $$
BEGIN
    -- Concatenate the specified columns based on instrument type and insert into ncdex_fo_scrip
    IF NEW.instrument_type = 'FUTCOM' THEN
        INSERT INTO ncdex_fo_scrip (symbol)
        VALUES (TRIM(REPLACE(NEW.underlying_commodity || NEW.expiry_date, ' ', '')));
    ELSIF NEW.instrument_type = 'OPTFUT' THEN
        INSERT INTO ncdex_fo_scrip (symbol)
        VALUES (TRIM(REPLACE(NEW.underlying_commodity || NEW.strike_price || NEW.option_type, ' ', '')));
    END IF;
    
    RETURN NULL; -- Return NULL as we don't need to return anything for triggers
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION nse_com_scrip_function()
RETURNS TRIGGER AS $$
BEGIN
    -- Concatenate the specified columns based on instrument and insert into nse_com_scrip
    IF NEW.instrument = 'FUTBAS' THEN
        INSERT INTO nse_com_scrip (symbol)
        VALUES (TRIM(REPLACE(NEW.symbol || NEW.expiry_dt, ' ', '')));
    ELSIF NEW.instrument = 'OPTFUT' THEN
        INSERT INTO nse_com_scrip (symbol)
        VALUES (TRIM(REPLACE(NEW.symbol || NEW.expiry_dt || NEW.strike_pr || NEW.option_typ, ' ', '')));
    END IF;
    
    RETURN NULL; -- Return NULL as we don't need to return anything for triggers
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION nse_fo_scrip_function()
RETURNS TRIGGER AS $$
BEGIN
    -- Concatenate the specified columns based on instrument type and insert into nse_fo_scrip
    IF NEW.instrument IN ('FUTIDX', 'FUTSTK') THEN
        INSERT INTO nse_fo_scrip (symbol)
        VALUES (TRIM(REPLACE(NEW.symbol || NEW.expiry_dt, ' ', '')));
    ELSIF NEW.instrument IN ('OPTIDX', 'OPTSTK') THEN
        INSERT INTO nse_fo_scrip (symbol)
        VALUES (TRIM(REPLACE(NEW.symbol || NEW.expiry_dt || NEW.strike_pr || NEW.option_typ, ' ', '')));
    END IF;
    
    RETURN NULL; -- Return NULL as we don't need to return anything for triggers
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION nse_curr_scrip_function()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO nse_curr_scrip (symbol)
    VALUES (TRIM(NEW.contract_d));
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION nse_curr_opt_scrip_function()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO nse_curr_opt_scrip (symbol)
    VALUES (TRIM(NEW.contract_d));
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION nse_eq_scrip_function()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO nse_eq_scrip (symbol)
    VALUES (TRIM(NEW.symbol));
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION nse_idx_scrip_function()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO nse_idx_scrip (symbol)
    VALUES (TRIM(NEW.index_name));
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION amfi_scrip_function()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO amfi_scrip (symbol)
    VALUES (TRIM(NEW.fund_name));
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;



-- Create the triggers
CREATE TRIGGER bse_eq_scrip_trigger AFTER INSERT ON bse_eq FOR EACH ROW EXECUTE FUNCTION bse_eq_scrip_function();
CREATE TRIGGER ncdex_scrip_trigger AFTER INSERT ON ncdex_bhav FOR EACH ROW EXECUTE FUNCTION ncdex_scrip_function();
CREATE TRIGGER bse_fo_scrip_trigger AFTER INSERT ON bse_fo_bhav FOR EACH ROW EXECUTE FUNCTION bse_fo_scrip_function();
CREATE TRIGGER nse_corp_scrip_trigger AFTER INSERT ON nse_corp FOR EACH ROW EXECUTE FUNCTION nse_corp_scrip_function();
CREATE TRIGGER ncdex_fo_scrip_trigger AFTER INSERT ON ncdex_fo FOR EACH ROW EXECUTE FUNCTION ncdex_fo_scrip_function();
CREATE TRIGGER nse_com_scrip_trigger AFTER INSERT ON nse_com_bhav FOR EACH ROW EXECUTE FUNCTION nse_com_scrip_function();
CREATE TRIGGER nse_fo_scrip_trigger AFTER INSERT ON nse_fo_bhav FOR EACH ROW EXECUTE FUNCTION nse_fo_scrip_function();
CREATE TRIGGER nse_curr_scrip_trigger AFTER INSERT ON nse_curr_fo_bhav FOR EACH ROW EXECUTE FUNCTION nse_curr_scrip_function();
CREATE TRIGGER nse_curr_opt_scrip_trigger AFTER INSERT ON nse_curr_op_bhav FOR EACH ROW EXECUTE FUNCTION nse_curr_opt_scrip_function();
CREATE TRIGGER nse_eq_scrip_trigger AFTER INSERT ON nse_eq FOR EACH ROW EXECUTE FUNCTION nse_eq_scrip_function();
CREATE TRIGGER nse_idx_scrip_trigger AFTER INSERT ON nse_ind_close_all FOR EACH ROW EXECUTE FUNCTION nse_idx_scrip_function();
CREATE TRIGGER amfi_scrip_trigger AFTER INSERT ON amfi_mf FOR EACH ROW EXECUTE FUNCTION amfi_scrip_function();
