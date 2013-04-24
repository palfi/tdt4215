INDEX_FILE     = index.tex
PDF_FILE       = report.pdf
BUILD_DIR      = build
PDF_VIEWER     = xdg-open
RUBBER_FLAGS   = --inplace \
	         --into=$(BUILD_DIR) \
	         --maxerr=-1 \
	         --short \
	         --force \
	         --warn=all \
		 --pdf

TEXCOUNT_FLAGS = -utf8 -total -inc -sum

FIGURES       = system-overview \
		class-diagram
FIGURES_DIR   = figures

SVG_TO_PDF    = $(foreach x,$(FIGURES),$(FIGURES_DIR)/$(x).pdf)

WORD_COUNT = $(shell texcount $(TEXCOUNT_FLAGS) $(INDEX_FILE) |\
	     grep 'Sum count:' | cut -d: -f2 | sed 's/^[ ]//g')

%.pdf: %.svg
	@echo "*** Converting SVG to PDF ***"
	@inkscape --without-gui --file=$< --export-pdf=$@

build: clean $(SVG_TO_PDF)
	@echo "*** Building pdf file from LaTeX files ***"
	@mkdir -p $(BUILD_DIR)
	$(shell sed -i "s/Word count: \([0-9]*\)/Word count: $(WORD_COUNT)/g" $(INDEX_FILE))
	@rubber $(RUBBER_FLAGS) $(INDEX_FILE)
	@echo "*** Running makeindex for nomenclature ***"
	@makeindex $(BUILD_DIR)/$(INDEX_FILE:.tex=.nlo) \
		-s nomencl.ist \
		-o $(BUILD_DIR)/$(INDEX_FILE:.tex=.nls)
	@echo "*** Rebuilding pdf file ***"
	@rubber $(RUBBER_FLAGS) $(INDEX_FILE)
	@mv $(BUILD_DIR)/$(INDEX_FILE:.tex=.pdf) $(PDF_FILE)

view: build
	@echo "*** Viewing pdf ***"
	$(PDF_VIEWER) $(PDF_FILE)

clean:
	@echo "*** Removing LaTeX build files ***"
	@rm -rf $(BUILD_DIR)
	@rm -f $(SVG_TO_PDF)
